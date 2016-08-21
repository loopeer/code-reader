package com.loopeer.codereader.sync;

public class DownloadProgressHelper {
    private static final String TAG = "DownloadProgressHelper";

    /*public static Subscription checkDownloadingProgress(Context context) {
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
                            if (repo.factor > 0) {
                                CoReaderDbHelper.getInstance(CodeReaderApplication.getAppContext())
                                        .updateRepoDownloadProgress(repo.downloadId, repo.factor);
                                RxBus.getInstance().send(new DownloadProgressEvent(repo.id,
                                        repo.downloadId, repo.factor, repo.isUnzip));
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
    }*/

}
