package com.loopeer.codereader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {

    public static final String PREF_FONT_SIZE = "pref_font_size";
    public static final String PREF_DISPLAY_LINE_NUMBER = "pref_display_line_number";
    public static final String PREF_MONOSPACEFONT = "pref_monospacefont";
    public static final String PREF_THEME = "pref_theme";

    public static float getPrefFontSize(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getFloat(PREF_FONT_SIZE, 12f);
    }

    public static void setPrefFontSize(final Context context, float size) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putFloat(PREF_FONT_SIZE, size).commit();
    }

    public static String getPrefTheme(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_THEME, "Default");
    }

    public static void setPrefTheme(final Context context, String theme) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_THEME, theme).commit();
    }

    public static boolean getPrefMonospacefont(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_MONOSPACEFONT, true);
    }

    public static void setPrefMonospacefont(final Context context, boolean b) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_MONOSPACEFONT, b).commit();
    }

    public static boolean getPrefDisplayLineNumber(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_DISPLAY_LINE_NUMBER, true);
    }

    public static void setPrefDisplayLineNumber(final Context context, boolean b) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_DISPLAY_LINE_NUMBER, b).commit();
    }

}