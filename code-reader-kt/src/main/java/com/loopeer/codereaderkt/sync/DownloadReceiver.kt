package com.loopeer.codereaderkt.sync

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.loopeer.codereaderkt.Navigator


class DownloadReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1!!.hasExtra(DownloadManager.EXTRA_DOWNLOAD_ID)) {
            var download: Long = p1.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, 0
            )
            if (download > 0) {
                var i: Intent = Intent(p0, DownloadRepoService::class.java)
                i.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, download)
                i.putExtra(Navigator.EXTRA_DOWNLOAD_SERVICE_TYPE, DownloadRepoService.DOWNLOAD_COMPLETE)
                p0!!.startService(i)
            }
        }
    }

}