package com.loopeer.codereader;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

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
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static CodeReaderApplication getInstance() {
        return mInstance;
    }

}
