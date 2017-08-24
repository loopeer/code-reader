package com.loopeer.codereaderkt.utils

import android.content.Context
import android.util.TypedValue
import com.loopeer.codereaderkt.CodeReaderApplications

object DeviceUtils {

    val statusBarHeight: Int
        get() {
            var result = 0
            val resId = CodeReaderApplications.appContext
                    .resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resId > 0) {
                result = CodeReaderApplications.appContext
                        .resources.getDimensionPixelOffset(resId)
            }
            return result
        }

    fun dpToPx(context: Context, dpValue: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, metrics)
    }

}

