package com.loopeer.codereader.sync;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;

import com.loopeer.codereader.CodeReaderApplication;
import com.loopeer.codereader.DownloadProgressEvent;
import com.loopeer.codereader.coreader.db.CoReaderDbHelper;
import com.loopeer.codereader.model.Repo;
import com.loopeer.codereader.utils.RxBus;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DownloadProgressHelper {

    public static Subscription checkDownloadingProgress(Context context) {
        return Observable.create(new Observable.OnSubscribe<List<Repo>>() {
            @Override
            public void call(Subscriber<? super List<Repo>> subscriber) {
                DownloadManager downloadManager =
                        (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                boolean downloading = true;
                while (downloading) {
                    int downloadNum = 0;
                    List<Repo> repos =
                            CoReaderDbHelper.getInstance(CodeReaderApplication.getAppContext()).readRepos();
                    for (Repo repo : repos) {
                        if (repo.isDownloading()) {
                            DownloadManager.Query q = new DownloadManager.Query();
                            q.setFilterById(repo.downloadId);

                            Cursor cursor = downloadManager.query(q);
                            cursor.moveToFirst();
                            int bytes_downloaded = cursor.getInt(cursor
                                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) != DownloadManager.STATUS_SUCCESSFUL) {
                                ++downloadNum;
                            }

                            final float dl_progress = 1.f * bytes_downloaded / bytes_total;
                            repo.factor = dl_progress;
                            CoReaderDbHelper.getInstance(CodeReaderApplication.getAppContext())
                                    .updateRepoDownloadProgress(repo.downloadId, repo.factor);
                            RxBus.getInstance().send(new DownloadProgressEvent(repo.downloadId, repo.factor));
                            cursor.close();
                        }
                    }
                    if (downloadNum == 0) {
                        downloading = false;
                    }
                    try {
                        Thread.currentThread().sleep(2000);
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

}
