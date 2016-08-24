package com.loopeer.codereader;

import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.loopeer.codereader.utils.ThemeUtils;

public class CodeReaderApplication extends BaseApp {
    private static BaseApp mInstance;
    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sAppContext = getApplicationContext();
        AppCompatDelegate.setDefaultNightMode(ThemeUtils.getCurrentNightMode(this));
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static BaseApp getInstance() {
        return mInstance;
    }

}
