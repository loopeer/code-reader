package com.loopeer.codereaderkt.sync

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.loopeer.codereaderkt.utils.DownloadUrlParser


class RemoteRepoFetchers(private val mContext: Context, private val mUrl: String?, private val mRepoName: String?) {
    private val mDestinationUri: Uri = Uri.fromFile(DownloadUrlParser.getRemoteRepoZipFileName(mRepoName.toString()))

    fun download(): Long {
        val manager = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(mUrl)
        val request = DownloadManager.Request(downloadUri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setVisibleInDownloadsUi(false)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        request.setDescription(mRepoName)
        request.setDestinationUri(mDestinationUri)
        return manager.enqueue(request)
    }

}