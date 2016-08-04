package com.loopeer.codereader;

import android.content.Context;
import android.content.Intent;

import com.loopeer.codereader.ui.activity.CodeReadActivity;

public class Navigator {

    public static void startCodeReadActivity(Context context) {
        Intent intent = new Intent(context, CodeReadActivity.class);
        context.startActivity(intent);
    }
}
