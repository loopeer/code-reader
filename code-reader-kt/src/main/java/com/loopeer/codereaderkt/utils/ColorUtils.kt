package com.loopeer.codereaderkt.utils

import android.content.Context
import android.support.v4.content.ContextCompat


object ColorUtils {

    fun getColorString(context: Context, res: Int): String {
        return String.format("#%06X", 0xFFFFFF and ContextCompat.getColor(context, res))
    }
}