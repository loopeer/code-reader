package com.loopeer.codereader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import java.util.HashMap;
import java.util.Map;

public class Settings
        extends SettingsBase {
    public Map<String, String> customFileToJSMap;
    public boolean displayLineNumbers = true;
    public boolean exitOnClose;
    public float fontSize;
    public String lastPath;
    public boolean monospaceFont = true;
    public boolean showCloseConfirmation = true;
    public String theme;
    public boolean useSystemFileBrowser;

    public Settings(Context paramContext) {
        super(paramContext);
    }

    protected void firstRunAfterUpdate(Double paramDouble1, Double paramDouble2) {
    }

    protected void firstRunInit() {
    }

    protected SharedPreferences getSettings(Context paramContext) {
        return paramContext.getSharedPreferences("CodePeeker", 0);
    }

    protected void load(SharedPreferences paramSharedPreferences) {
        this.displayLineNumbers = paramSharedPreferences.getBoolean("displayLineNumbers", true);
        this.monospaceFont = paramSharedPreferences.getBoolean("monospaceFont", true);
        this.useSystemFileBrowser = paramSharedPreferences.getBoolean("useSystemFileBrowser", false);
        this.lastPath = paramSharedPreferences.getString("lastPath", Environment.getExternalStorageDirectory().getPath());
        this.showCloseConfirmation = paramSharedPreferences.getBoolean("showCloseConfirmation", true);
        this.exitOnClose = paramSharedPreferences.getBoolean("exitOnClose", false);
        this.fontSize = paramSharedPreferences.getFloat("fontSize", 12.0F);
        this.theme = "Default";
        this.customFileToJSMap = new HashMap();
    }

    protected void save(SharedPreferences.Editor paramEditor) {
        paramEditor.putBoolean("displayLineNumbers", this.displayLineNumbers);
        paramEditor.putBoolean("monospaceFont", this.monospaceFont);
        paramEditor.putBoolean("useSystemFileBrowser", this.useSystemFileBrowser);
        paramEditor.putString("lastPath", this.lastPath);
        paramEditor.putBoolean("showCloseConfirmation", this.showCloseConfirmation);
        paramEditor.putBoolean("exitOnClose", this.exitOnClose);
        paramEditor.putFloat("fontSize", this.fontSize);
    }
}
