package com.loopeer.codereader.ui.activity;

import android.content.Context;
import android.content.Intent;

public class Navigator {

    public static void startCodeReadActivity(Context context) {
        Intent intent = new Intent(context, CodeReadActivity.class);
        context.startActivity(intent);
    }
}
