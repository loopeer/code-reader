package com.loopeer.codereader.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.loopeer.codereader.Navigator;
import com.loopeer.codereader.api.ServiceFactory;
import com.loopeer.codereader.api.service.GithubService;
import com.loopeer.codereader.coreader.db.CoReaderDbHelper;
import com.loopeer.codereader.model.Repo;
import com.loopeer.codereader.utils.FileCache;
import com.loopeer.codereader.utils.Unzip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class DownloadRepoService extends Service {
    private static final String TAG = "DownloadRepoService";

    private GithubService mGithubService = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mGithubService = ServiceFactory.getGithubService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        parseIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void parseIntent(Intent intent) {
        Intent in = intent;
        String url = in.getStringExtra(Navigator.EXTRA_DOWNLOAD_URL);
        downloadFile(url);
    }

    private void downloadFile(String url) {
        String downloadUrl = parseUrl(url);
        FileCache fileCache = FileCache.getInstance();
        CoReaderDbHelper.getInstance(getApplicationContext()).insertRepo(new Repo(getRepoMasterName(url)
                , fileCache.getCacheDir().getPath() + File.separator + getRepoMasterName(url), url, true));
        if (downloadUrl == null) return;
        mGithubService.downloadRepo(downloadUrl)
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.toString());

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        writeResponseBodyToDisk(responseBody, url);
                    }
                });
    }

    public String getRepoMasterName(String url) {
        String name = getRepoName(url);
        return name + "-master";
    }

    private String getRepoNameZip(String url) {
        String[] strings = url.split("/");
        return strings[4] + ".zip";
    }

    private String getRepoName(String url) {
        String[] strings = url.split("/");
        return strings[4].split("//.")[0];
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String url) {
        try {
            FileCache fileCache = FileCache.getInstance();
            File file = fileCache.getDownloadRepoFile(getRepoName(url));
            File zipFile = fileCache.getDownloadRepoFile(getRepoNameZip(url));
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[1024 * 8];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(zipFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();
                Unzip decomp = new Unzip(zipFile.getPath()
                        , fileCache.getCacheDir().getPath() + File.separator, getApplicationContext());
                decomp.DecompressZip();
                if (zipFile.exists()) zipFile.delete();
                return true;
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return false;
        }
    }

    private String parseUrl(String url) {
        if (TextUtils.isEmpty(url)) return null;
        StringBuilder sb = new StringBuilder();
        String[] strings = url.split("/");
        if (strings.length < 5) return null;
        if (strings.length == 5) {
            for (int i = 0; i < strings.length; i++) {
                if (i == 4) {
                    if (strings[i].contains("?")) {
                        String[] lastName = strings[i].split("\\?");
                        sb.append(lastName[0]);
                        sb.append("/");
                    }
                }
                sb.append(strings[i]);
                sb.append("/");
            }
            sb.append("archive/master.zip");
            return sb.toString();
        }
        if (strings.length > 5) {
            for (int i = 0; i < 5; i++) {
                sb.append(strings[i]);
                sb.append("/");
            }
            sb.append("archive/master.zip");
            return sb.toString();
        }
        return null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
