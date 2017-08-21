package com.loopeer.codereaderkt.utils

import android.content.Context
import android.preference.PreferenceManager


object PrefUtils {

    val PREF_FONT_SIZE = "pref_font_size"
    val PREF_DISPLAY_LINE_NUMBER = "pref_display_line_number"
    val PREF_MENLO_FONT = "pref_menlo_font"
    val PREF_THEME = "pref_theme"

    fun getPrefFontSize(context: Context): Float {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getFloat(PREF_FONT_SIZE, 12f)
    }

    fun setPrefFontSize(context: Context, size: Float) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        sp.edit().putFloat(PREF_FONT_SIZE, size).apply()
    }

    @JvmStatic
    fun getPrefTheme(context: Context): String {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getString(PREF_THEME, "Default")
    }

    fun setPrefTheme(context: Context, theme: String) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        sp.edit().putString(PREF_THEME, theme).apply()
    }

    fun getPrefMenlofont(context: Context): Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getBoolean(PREF_MENLO_FONT, true)
    }

    fun setPrefMenlofont(context: Context, b: Boolean) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        sp.edit().putBoolean(PREF_MENLO_FONT, b).apply()
    }

    fun getPrefDisplayLineNumber(context: Context): Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getBoolean(PREF_DISPLAY_LINE_NUMBER, true)
    }

    fun setPrefDisplayLineNumber(context: Context, b: Boolean) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        sp.edit().putBoolean(PREF_DISPLAY_LINE_NUMBER, b).apply()
    }

}