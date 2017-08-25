package com.loopeer.codereaderkt.sync

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.event.DownloadRepoMessageEvent
import com.loopeer.codereaderkt.utils.DownloadUrlParser
import com.loopeer.codereaderkt.utils.RxBus


class RemoteRepoFetchers(private val mContext: Context, private val mUrl: String?, private val mRepoName: String?) {
    private val mDestinationUri: Uri = Uri.fromFile(DownloadUrlParser.getRemoteRepoZipFileName(mRepoName.toString()))

    fun download(): Long {
        val manager = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(mUrl)

        var request:DownloadManager.Request ?=  null
        try {
            request = DownloadManager.Request(downloadUri);
        } catch ( e:IllegalArgumentException) {
            RxBus.getInstance().send(DownloadRepoMessageEvent(mContext.getString(R.string.repo_download_url_parse_error)));
            return -1;
        }
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setVisibleInDownloadsUi(false)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        request.setDescription(mRepoName)
        request.setDestinationUri(mDestinationUri)
        return manager.enqueue(request)
    }

}