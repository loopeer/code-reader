package com.loopeer.codereader.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

public class ColorUtils {

    public static String getColorString(Context context, int res) {
        return String.format("#%06X"
                , 0xFFFFFF & ContextCompat.getColor(context
                        , res));
    }
}
