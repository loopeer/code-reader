package com.loopeer.codereader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

public abstract class SettingsBase {
    public boolean isFirstRun;
    public boolean isFirstRunAfterUpdate;
    public boolean isPro;
    public String lastVersion;
    public String latestVersion;
    public String version;

    public SettingsBase(Context paramContext) {
        try {
            this.version = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionName;
            loadSettings(paramContext);
            return;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            for (; ; ) {
                localNameNotFoundException.printStackTrace();
            }
        }
    }

    protected abstract void firstRunAfterUpdate(String paramDouble1, Double paramDouble2);

    protected abstract void firstRunInit();

    protected SharedPreferences getSettings(Context paramContext) {
        return paramContext.getSharedPreferences(paramContext.getApplicationInfo().packageName, 0);
    }

    public boolean hasUpdate() {
        return Double.valueOf(this.version).doubleValue() < Double.valueOf(this.latestVersion).doubleValue();
    }

    protected abstract void load(SharedPreferences paramSharedPreferences);

    public void loadSettings(Context paramContext) {
        boolean bool = true;
        SharedPreferences preferences = getSettings(paramContext);
        this.latestVersion = preferences.getString("latestVersion", this.version);
        this.lastVersion = preferences.getString("lastVersion", this.version);
        this.isFirstRun = preferences.getBoolean("isFirstRun", true);
        if ((!this.isFirstRun) && (!this.lastVersion.equals(this.version))) {
        }
        for (; ; ) {
            this.isFirstRunAfterUpdate = bool;
            this.isPro = preferences.getBoolean("isPro", false);
            if (this.isFirstRun) {
                firstRunInit();
            }
            load(preferences);
            if (this.isFirstRunAfterUpdate) {
//
//                firstRunAfterUpdate(String.valueOf(this.lastVersion), Double.valueOf(this.version));
            }
            bool = false;
            return;
        }
    }

    protected abstract void save(SharedPreferences.Editor paramEditor);

    public void saveSettings(Context paramContext) {
        SharedPreferences.Editor editor = getSettings(paramContext).edit();
        save(editor);
        if (this.isFirstRun) {
            editor.putBoolean("isFirstRun", false);
        }
        if (!this.lastVersion.equals(this.version)) {
            editor.putString("lastVersion", this.version);
        }
        editor.putString("latestVersion", this.latestVersion);
        editor.putBoolean("isPro", this.isPro);
        editor.commit();
    }
}
