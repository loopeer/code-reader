package com.loopeer.codereader.utils;

import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.loopeer.codereader.R;

public class SnackbarUtils {

    public static void show(View view,String s){
        Snackbar.make(view,s,Snackbar.LENGTH_SHORT).show();
    }

    public static void show(View view, @StringRes int id){
        Snackbar.make(view,id,Snackbar.LENGTH_SHORT).show();
    }
}
