package com.loopeer.codereader.sync;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.loopeer.codereader.CodeReaderApplication;
import com.loopeer.codereader.DownloadProgressEvent;
import com.loopeer.codereader.Navigator;
import com.loopeer.codereader.coreader.db.CoReaderDbHelper;
import com.loopeer.codereader.model.Repo;
import com.loopeer.codereader.utils.FileCache;
import com.loopeer.codereader.utils.RxBus;
import com.loopeer.codereader.utils.Unzip;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

public class DownloadRepoService extends Service {
    private static final String TAG = "DownloadRepoService";

    private List<Long> mDownloadRepoIds;

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadRepoIds = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        parseIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void parseIntent(Intent intent) {
        Intent in = intent;
        String url = in.getStringExtra(Navigator.EXTRA_DOWNLOAD_URL);
        Repo repo = (Repo) in.getSerializableExtra(Navigator.EXTRA_REPO);
        long id = in.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        if (TextUtils.isEmpty(url)) {
            doRepoDownloadComplete(id);
        } else {
            downloadFile(url, repo);
        }
    }

    private void doRepoDownloadComplete(long id) {
        CoReaderDbHelper.getInstance(CodeReaderApplication.getAppContext())
                .updateRepoDownloadProgress(id, 1, true);
        RxBus.getInstance().send(new DownloadProgressEvent(id, true));

        Observable.create((Observable.OnSubscribe<Void>) subscriber -> {
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
                        Unzip decomp = new Unzip(zipFile.getPath()
                                , fileCache.getCacheDir().getPath() + File.separator + name, getApplicationContext());
                        decomp.DecompressZip();
                        if (zipFile.exists()) zipFile.delete();
                        RxBus.getInstance().send(new DownloadProgressEvent(id, false));
                        Log.d(TAG, "Unzip Success");
                    } else {
                    }
                }
                mDownloadRepoIds.remove(id);
                subscriber.onCompleted();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void downloadFile(String url, Repo repo) {
        RemoteRepoFetcher dataFetcher = new RemoteRepoFetcher(this, url, repo.name);
        long downloadId = dataFetcher.download();
        CoReaderDbHelper.getInstance(getApplicationContext()).updateRepoDownloadId(downloadId, repo.id);
        mDownloadRepoIds.add(downloadId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
