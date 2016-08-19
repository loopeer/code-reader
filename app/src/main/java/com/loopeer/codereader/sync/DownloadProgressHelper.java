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
                final boolean[] downloading = {true};
                while (downloading[0]) {
                    List<Repo> repos =
                            CoReaderDbHelper.getInstance(CodeReaderApplication.getAppContext()).readRepos();
                    int downloadNum = 0;
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
                                final float dl_progress = 1f * bytes_downloaded / bytes_total;
                                repo.factor = dl_progress;
                            } else {
                                repo.factor = 1;
                            }
                            if (repo.factor > 0) {
                                if (repo.factor == 1f) repo.isUnzip = true;
                                CoReaderDbHelper.getInstance(CodeReaderApplication.getAppContext())
                                        .updateRepoDownloadProgress(repo.downloadId, repo.factor);
                                RxBus.getInstance().send(new DownloadProgressEvent(repo.downloadId, repo.factor, repo.isUnzip));
                            }
                            cursor.close();
                        }
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
                        Thread.currentThread().sleep(1000);
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
