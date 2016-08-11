package com.loopeer.codereader.sync;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.loopeer.codereader.Navigator;

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(DownloadManager.EXTRA_DOWNLOAD_ID)) {
            long downloadId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            if (downloadId > 0) {
                Intent i = new Intent(context, DownloadRepoService.class);
                i.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, downloadId);
                context.startService(i);
            }
        }

        if (intent.hasExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS)) {
            Navigator.startMainActivity(context);
        }
    }

}
