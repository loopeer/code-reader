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

import com.loopeer.codereader.Navigator;
import com.loopeer.codereader.utils.FileCache;
import com.loopeer.codereader.utils.Unzip;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadRepoService extends Service {
    private static final String TAG = "DownloadRepoService";

    private List<Long> mDownloadIds;

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadIds = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        parseIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void parseIntent(Intent intent) {
        Intent in = intent;
        String url = in.getStringExtra(Navigator.EXTRA_DOWNLOAD_URL);
        long id = in.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        if (TextUtils.isEmpty(url)) {
            doRepoDownloadComplete(id);
        } else {
            downloadFile(url);
        }
    }

    private void doRepoDownloadComplete(long id) {
        Cursor cursor = null;
        try {
            DownloadManager manager =
                    (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query baseQuery = new DownloadManager.Query()
                    .setFilterById(id);

            cursor = manager.query(baseQuery);

            final int statusColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
            final int localFilenameColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_FILENAME);
            if (cursor.moveToNext()) {
                final long status = cursor.getLong(statusColumnId);
                final String path = cursor.getString(localFilenameColumnId);
                if (status == DownloadManager.STATUS_SUCCESSFUL) { // 下载成功
                    File zipFile = new File(path);
                    FileCache fileCache = FileCache.getInstance();
                    Unzip decomp = new Unzip(zipFile.getPath()
                            , fileCache.getCacheDir().getPath() + File.separator, getApplicationContext());
                    decomp.DecompressZip();
                    if (zipFile.exists()) zipFile.delete();
                    Log.e(TAG, "Success");
                } else {
                }
            }
            mDownloadIds.remove(id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    private void downloadFile(String url) {
        RemoteRepoFetcher dataFetcher = new RemoteRepoFetcher(this, url);
        mDownloadIds.add(dataFetcher.download());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
