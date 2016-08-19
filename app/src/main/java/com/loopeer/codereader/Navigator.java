package com.loopeer.codereader;

import android.content.Context;
import android.content.Intent;

import com.loopeer.codereader.model.Repo;
import com.loopeer.codereader.sync.DownloadRepoService;
import com.loopeer.codereader.ui.activity.CodeReadActivity;
import com.loopeer.codereader.ui.activity.MainActivity;
import com.loopeer.codereader.ui.activity.SearchActivity;
import com.loopeer.codereader.ui.activity.SimpleWebActivity;

public class Navigator {

    public final static String EXTRA_REPO = "extra_repo";
    public final static String EXTRA_DIRETORY_ROOT = "extra_diretory_root";
    public final static String EXTRA_DIRETORY_ROOT_NODE_INSTANCE = "extra_diretory_root_node_instance";
    public final static String EXTRA_DIRETORY_SELECTING = "extra_diretory_selecting";
    public final static String EXTRA_WEB_URL = "extra_web_url";
    public final static String EXTRA_HTML_STRING = "extra_html_string";
    public final static String EXTRA_DOWNLOAD_URL = "extra_download_url";
    public final static String EXTRA_DOWNLOAD_REPO_NAME = "extra_download_repo_name";
    public final static String EXTRA_DOWNLOAD_REPO_ID = "extra_download_repo_id";

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    public static void startDownloadRepoService(Context context, Repo repo) {
        Intent intent = new Intent(context, DownloadRepoService.class);
        intent.putExtra(EXTRA_REPO, repo);
        context.startService(intent);
    }

    public static void startSearchActivity(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

}
