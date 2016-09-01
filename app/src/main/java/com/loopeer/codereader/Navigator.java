package com.loopeer.codereader;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.widget.Toast;

import com.loopeer.codereader.coreader.db.CoReaderDbHelper;
import com.loopeer.codereader.model.Repo;
import com.loopeer.codereader.sync.DownloadRepoService;
import com.loopeer.codereader.ui.activity.AboutActivity;
import com.loopeer.codereader.ui.activity.AddRepoActivity;
import com.loopeer.codereader.ui.activity.CodeReadActivity;
import com.loopeer.codereader.ui.activity.MainActivity;
import com.loopeer.codereader.ui.activity.SearchActivity;
import com.loopeer.codereader.ui.activity.SettingActivity;
import com.loopeer.codereader.ui.activity.SimpleWebActivity;

public class Navigator {

    public final static String EXTRA_REPO = "extra_repo";
    public final static String EXTRA_ID = "extra_id";
    public final static String EXTRA_DOWNLOAD_SERVICE_TYPE = "extra_download_service_type";
    public final static String EXTRA_DIRETORY_ROOT = "extra_diretory_root";
    public final static String EXTRA_DIRETORY_ROOT_NODE_INSTANCE = "extra_diretory_root_node_instance";
    public final static String EXTRA_DIRETORY_SELECTING = "extra_diretory_selecting";
    public final static String EXTRA_WEB_URL = "extra_web_url";
    public final static String EXTRA_HTML_STRING = "extra_html_string";

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void startCodeReadActivity(Context context, Repo repo) {
        Intent intent = new Intent(context, CodeReadActivity.class);
        intent.putExtra(EXTRA_REPO, repo);
        context.startActivity(intent);
    }

    public static void startWebActivity(Context context, String url) {
        Intent intent = new Intent(context, SimpleWebActivity.class);
        intent.putExtra(EXTRA_WEB_URL, url);
        context.startActivity(intent);
    }

    public static void startAboutActivity(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    public static void startComposeEmail(Context context, String[] addresses, String subject, String content) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(content));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, R.string.about_email_app_not_have, Toast.LENGTH_SHORT).show();
        }
    }

    public static void startDownloadNewRepoService(Context context, Repo repo) {
        Repo sameRepo = CoReaderDbHelper.getInstance(context).readSameRepo(repo);
        long repoId;
        if (sameRepo != null) {
            repoId = Long.parseLong(sameRepo.id);
        } else {
            repoId = CoReaderDbHelper.getInstance(context).insertRepo(repo);
        }
        repo.id = String.valueOf(repoId);
        Navigator.startDownloadRepoService(context, repo);
    }

    public static void startDownloadRepoService(Context context, Repo repo) {
        Intent intent = new Intent(context, DownloadRepoService.class);
        intent.putExtra(EXTRA_REPO, repo);
        intent.putExtra(EXTRA_DOWNLOAD_SERVICE_TYPE, DownloadRepoService.DOWNLOAD_REPO);
        context.startService(intent);
    }

    public static void startDownloadRepoService(Context context, int type) {
        Intent intent = new Intent(context, DownloadRepoService.class);
        intent.putExtra(EXTRA_DOWNLOAD_SERVICE_TYPE, type);
        context.startService(intent);
    }

    public static void startDownloadRepoServiceRemove(Context context, long downloadId) {
        Intent intent = new Intent(context, DownloadRepoService.class);
        intent.putExtra(EXTRA_DOWNLOAD_SERVICE_TYPE, DownloadRepoService.DOWNLOAD_REMOVE_DOWNLOAD);
        intent.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, downloadId);
        context.startService(intent);
    }

    public static void startSearchActivity(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    public static void startAddRepoActivity(Context context) {
        Intent intent = new Intent(context, AddRepoActivity.class);
        context.startActivity(intent);
    }

    public static void startSettingActivity(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

}
