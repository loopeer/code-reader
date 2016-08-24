package com.loopeer.codereader.utils;

import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

public class ThemeUtils {
    public static final String THEME_DAY = "Default";
    public static final String THEME_NIGHT = "Night";

    @AppCompatDelegate.NightMode
    public static int getCurrentNightMode(Context context) {
        return PrefUtils.getPrefTheme(context).equals(THEME_DAY)
                ? AppCompatDelegate.MODE_NIGHT_NO
                : AppCompatDelegate.MODE_NIGHT_YES;
    }
}
