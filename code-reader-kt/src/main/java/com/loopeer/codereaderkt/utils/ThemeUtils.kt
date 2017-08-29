package com.loopeer.codereaderkt.utils

import android.content.Context
import android.support.v7.app.AppCompatDelegate


object ThemeUtils {
    val THEME_DAY = "Default"
    val THEME_NIGHT = "Night"

    @AppCompatDelegate.NightMode
    fun getCurrentNightMode(context: Context): Int {
        return if (PrefUtils.getPrefTheme(context) == THEME_DAY)
            AppCompatDelegate.MODE_NIGHT_NO
        else
            AppCompatDelegate.MODE_NIGHT_YES
    }
}
