package com.loopeer.codereader;

import android.app.Application;
import android.content.Context;

public class CodeReaderApplication extends Application {
    private static CodeReaderApplication mInstance;
    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sAppContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static CodeReaderApplication getInstance() {
        return mInstance;
    }

}
