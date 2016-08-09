package com.loopeer.codereader;

import android.content.Context;
import android.content.Intent;

import com.loopeer.codereader.model.Repo;
import com.loopeer.codereader.ui.activity.CodeReadActivity;
import com.loopeer.codereader.ui.activity.SimpleWebActivity;

public class Navigator {

    public final static int FILE_SELECT_CODE = 1000;
    public final static String EXTRA_REPO = "extra_repo";
    public final static String EXTRA_DIRETORY_ROOT = "extra_diretory_root";
    public final static String EXTRA_DIRETORY_ROOT_NODE_INSTANCE = "extra_diretory_root_node_instance";
    public final static String EXTRA_DIRETORY_SELECTING = "extra_diretory_selecting";
    public final static String EXTRA_WEB_URL = "extra_web_url";
    public final static String EXTRA_HTML_STRING = "extra_html_string";

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
}
