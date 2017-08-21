package com.loopeer.codereaderkt;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.facebook.stetho.Stetho;
import com.loopeer.codereaderkt.utils.ThemeUtils;

public class CodeReaderApplication extends Application {
    private static CodeReaderApplication mInstance;
    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sAppContext = getApplicationContext();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
        AppCompatDelegate.setDefaultNightMode(ThemeUtils.getCurrentNightMode(this));
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static CodeReaderApplication getInstance() {
        return mInstance;
    }

}
