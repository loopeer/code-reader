package com.loopeer.directorychooser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class NavigatorChooser {

    public final static int DIRECTORY_FILE_SELECT_CODE = 10000;
    public final static String EXTRA_FILE_NODE = "extra_file_node";

    public static void startDirectoryFileChooserActivity(Context context) {
        Intent intent = new Intent(context, DirectoryFileChooserActivity.class);
        ((Activity) context).startActivityForResult(intent, DIRECTORY_FILE_SELECT_CODE);
    }

}
