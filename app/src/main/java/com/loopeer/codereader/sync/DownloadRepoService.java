package com.loopeer.codereader.sync;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.loopeer.codereader.CodeReaderApplication;
import com.loopeer.codereader.Navigator;
import com.loopeer.codereader.R;
import com.loopeer.codereader.coreader.db.CoReaderDbHelper;
import com.loopeer.codereader.event.DownloadProgressEvent;
import com.loopeer.codereader.event.DownloadRepoMessageEvent;
import com.loopeer.codereader.model.Repo;
import com.loopeer.codereader.utils.FileCache;
import com.loopeer.codereader.utils.RxBus;
import com.loopeer.codereader.utils.Unzip;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DownloadRepoService extends Service {
    public static final int DOWNLOAD_COMPLETE = 0;
    public static final int DOWNLOAD_REPO = 1;
    public static final int DOWNLOAD_PROGRESS = 2;

    private static final String TAG = "DownloadRepoService";

    private HashMap<Long, Repo> mDownloadingRepos;
    private Subscription mProgressSubscription;

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadingRepos = new HashMap<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        parseIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void parseIntent(Intent intent) {
        Intent in = intent;
        int type = in.getIntExtra(Navigator.EXTRA_DOWNLOAD_SERVICE_TYPE, 0);
        Repo repo = (Repo) in.getSerializableExtra(Navigator.EXTRA_REPO);
        long id = in.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        switch (type) {
            case DOWNLOAD_COMPLETE:
                doRepoDownloadComplete(id);
                break;
            case DOWNLOAD_REPO:
                downloadFile(repo);
                break;
            case DOWNLOAD_PROGRESS:
                checkDownloadProgress();
                break;
        }
    }

    private void doRepoDownloadComplete(long id) {
        CoReaderDbHelper.getInstance(CodeReaderApplication.getAppContext())
                .updateRepoUnzipProgress(id, 1, true);
        RxBus.getInstance().send(new DownloadProgressEvent(id, true));
        Observable.create((Observable.OnSubscribe<Void>) subscriber -> {
            Cursor cursor = null;
            try {
                DownloadManager manager =
                        (DownloadManager) DownloadRepoService.this.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query baseQuery = new DownloadManager.Query()
                        .setFilterById(id);
                cursor = manager.query(baseQuery);
                final int statusColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
                final int localFilenameColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_FILENAME);
                final int descName = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_DESCRIPTION);
                if (cursor.moveToNext()) {
                    final long status = cursor.getLong(statusColumnId);
                    final String path = cursor.getString(localFilenameColumnId);
                    final String name = cursor.getString(descName);
                    if (status == DownloadManager.STATUS_SUCCESSFUL) { // 下载成功
                        File zipFile = new File(path);
                        FileCache fileCache = FileCache.getInstance();
                        fileCache.deleteFilesByDirectory(new File(fileCache.getCacheDir().getPath() + File.separator + name));
                        Unzip decomp = new Unzip(zipFile.getPath()
                                , fileCache.getCacheDir().getPath() + File.separator + name, getApplicationContext());
                        decomp.DecompressZip();
                        if (zipFile.exists()) zipFile.delete();
                        CoReaderDbHelper.getInstance(CodeReaderApplication.getAppContext())
                                .updateRepoUnzipProgress(id, 1, false);
                        RxBus.getInstance().send(new DownloadProgressEvent(id, false));
                    } else {
                    }
                }
                RxBus.getInstance().send(new DownloadRepoMessageEvent(
                        getString(R.string.repo_download_complete, mDownloadingRepos.get(id).name)));
                mDownloadingRepos.remove(id);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            } finally {
                cursor.close();
            }
        })
                .onErrorResumeNext(Observable.empty())
                .subscribeOn(Schedulers.io())
                .doOnError(e -> Log.d(TAG, e.toString()))
                .doOnCompleted(this::checkTaskEmptyToFinish)
                .subscribe();
    }

    private void checkTaskEmptyToFinish() {
        if (mDownloadingRepos.isEmpty()) {
            stopSelf();
        }
    }

    private void downloadFile(Repo repo) {
        RemoteRepoFetcher dataFetcher = new RemoteRepoFetcher(this, repo.netDownloadUrl, repo.name);
        long downloadId = dataFetcher.download();
        repo.downloadId = downloadId;
        mDownloadingRepos.put(downloadId, repo);
        CoReaderDbHelper.getInstance(getApplicationContext()).updateRepoDownloadId(downloadId, repo.id);
        RxBus.getInstance().send(new DownloadRepoMessageEvent(getString(R.string.repo_download_start)));
        checkDownloadProgress();

    }

    private void checkDownloadProgress() {

        if (mDownloadingRepos.isEmpty()) {
            List<Repo> repos = CoReaderDbHelper.getInstance(this).readRepos();
            for (Repo repo : repos) {
                if (repo.isDownloading()) {
                    mDownloadingRepos.put(repo.downloadId, repo);
                }
            }
        }
        if (mProgressSubscription != null && !mProgressSubscription.isUnsubscribed()) {
            mProgressSubscription.unsubscribe();
        }
        mProgressSubscription = checkDownloadingProgress(this);
        if (mDownloadingRepos.isEmpty()) stopSelf();
    }

    private void clearDownloadProgressSubscription() {
        if (mProgressSubscription != null && !mProgressSubscription.isUnsubscribed()) {
            mProgressSubscription.unsubscribe();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearDownloadProgressSubscription();
    }

    public Subscription checkDownloadingProgress(Context context) {
        return Observable.create(new Observable.OnSubscribe<List<Repo>>() {

            @Override
            public void call(Subscriber<? super List<Repo>> subscriber) {
                DownloadManager downloadManager =
                        (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                final boolean[] downloading = {true};
                while (downloading[0]) {
                    List<Repo> repos = new ArrayList(mDownloadingRepos.values());
                    if (repos.isEmpty()) break;
                    int downloadNum = 0;
                    for (Repo repo : repos) {
                        DownloadManager.Query q = new DownloadManager.Query();
                        q.setFilterById(repo.downloadId);

                        Cursor cursor = downloadManager.query(q);
                        cursor.moveToFirst();
                        int bytes_downloaded = cursor.getInt(cursor
                                .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int bytes_total = cursor.getInt(
                                cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));


                        //TODO
                        String reasonString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        Log.e(TAG, "reason: " + reasonString + "    status : " + status);
                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                                != DownloadManager.STATUS_SUCCESSFUL) {
                            ++downloadNum;
                            final float dl_progress = 1f * bytes_downloaded / bytes_total;
                            repo.factor = dl_progress;
                        } else {
                            repo.factor = 1;
                        }
                        if (repo.factor < 0) repo.factor = 0;
                        if (repo.factor >= 0) {
                            CoReaderDbHelper.getInstance(CodeReaderApplication.getAppContext())
                                    .updateRepoDownloadProgress(repo.downloadId, repo.factor);
                            RxBus.getInstance().send(new DownloadProgressEvent(repo.id,
                                    repo.downloadId, repo.factor, repo.isUnzip));
                        }
                        cursor.close();
                    }
                    if (downloadNum == 0) {
                        downloading[0] = false;
                    }
                    subscriber.add(new Subscription() {
                        @Override
                        public void unsubscribe() {
                            downloading[0] = false;
                            return;
                        }

                        @Override
                        public boolean isUnsubscribed() {
                            return false;
                        }
                    });
                    try {
                        Thread.currentThread().sleep(500);
                    } catch (InterruptedException e) {
                        subscriber.onError(e);
                    }
                }
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
