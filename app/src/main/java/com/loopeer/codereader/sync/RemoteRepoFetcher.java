
package com.loopeer.codereader.sync;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import com.loopeer.codereader.R;
import com.loopeer.codereader.event.DownloadRepoMessageEvent;
import com.loopeer.codereader.utils.DownloadUrlParser;
import com.loopeer.codereader.utils.RxBus;

public class RemoteRepoFetcher {
    private Context mContext;
    private String mUrl;
    private Uri mDestinationUri;
    private String mRepoName;

    public RemoteRepoFetcher(Context context, String url, String repoName) {
        mContext = context;
        mUrl = url;
        mRepoName = repoName;
        mDestinationUri = Uri.fromFile(DownloadUrlParser.getRemoteRepoZipFileName(repoName));
    }

    public long download() {
        DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(mUrl);
        DownloadManager.Request request = null;
        try {
            request = new DownloadManager.Request(downloadUri);
        } catch (IllegalArgumentException e) {
            RxBus.getInstance().send(new DownloadRepoMessageEvent(mContext.getString(R.string.repo_download_url_parse_error)));
            return -1;
        }
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(false);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE| DownloadManager.Request.NETWORK_WIFI);
        request.setDescription(mRepoName);
        request.setDestinationUri(mDestinationUri);
        long downloadId = manager.enqueue(request);
        return downloadId;
    }

}