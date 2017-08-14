package com.loopeer.codereaderkt.utils

import android.text.TextUtils
import java.util.regex.Pattern


object CustomTextUtils {

    fun calculateTextStartEnd(strings: String, target: String): IntArray {
        val result = IntArray(2)
        result[0] = strings.indexOf(target)
        result[1] = result[0] + target.length
        return result
    }


    fun isEmail(email: String): Boolean {
        if (TextUtils.isEmpty(email)) return false
        val p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")//复杂匹配
        val m = p.matcher(email)
        return m.matches()
    }
}
