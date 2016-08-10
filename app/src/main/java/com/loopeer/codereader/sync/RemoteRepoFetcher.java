
package com.loopeer.codereader.sync;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.loopeer.codereader.coreader.db.CoReaderDbHelper;
import com.loopeer.codereader.model.Repo;
import com.loopeer.codereader.utils.FileCache;

import java.io.File;

public class RemoteRepoFetcher {

  private static final String EXTENSION = ".zip";
  private Context mContext;
  private String mDownloadUrl;
  private String mUrl;
  private Uri mDestinationUri;
  private static final String REPO_URL_BASE = "https://codeload.github.com/";

  public RemoteRepoFetcher(Context context, String url) {
    mContext = context;
    mUrl = url;
    mDownloadUrl = parseUrl(url);
    FileCache fileCache = FileCache.getInstance();
    File destinationFile = fileCache.getRemoteRepoZipFileFromUrl(url);
    mDestinationUri = Uri.fromFile(destinationFile);
  }

  public long download() {
    FileCache fileCache = FileCache.getInstance();
    String repoName = fileCache.getRepoMasterName(mUrl);
    CoReaderDbHelper.getInstance(mContext).insertRepo(new Repo(repoName
            , fileCache.getCacheDir().getPath() + File.separator + repoName, mUrl, true));

    DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
    Uri downloadUri = Uri.parse(mDownloadUrl);
    DownloadManager.Request request = new DownloadManager.Request(downloadUri);
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
    request.setDescription(repoName);
    request.setDestinationUri(mDestinationUri);
    return manager.enqueue(request);
  }

  private String parseUrl(String url) {
    if (TextUtils.isEmpty(url)) return null;
    StringBuilder sb = new StringBuilder();
    String[] strings = url.split("/");
    if (strings.length < 5) return null;
    if (strings.length == 5) {
      sb.append(REPO_URL_BASE);
      sb.append(strings[3]);
      sb.append("/");
      if (strings[4].contains("?")) {
        String[] lastName = strings[4].split("\\?");
        sb.append(lastName[0]);
        sb.append("/");
      } else {
        sb.append(strings[4]);
        sb.append("/");
      }
      sb.append("zip/master");
      return sb.toString();
    }
    if (strings.length > 5) {
      sb.append(REPO_URL_BASE);
      sb.append(strings[3]);
      sb.append("/");
      sb.append(strings[4]);
      sb.append("/");
      sb.append("zip/master");
      return sb.toString();
    }
    return null;
  }

}