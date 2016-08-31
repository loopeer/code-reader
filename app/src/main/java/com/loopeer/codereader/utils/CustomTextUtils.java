package com.loopeer.codereader.utils;

import android.text.TextUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomTextUtils {

  public static int[] calculateTextStartEnd(String strings, String target) {
    int[] result = new int[2];
    result[0] = strings.indexOf(target);
    result[1] = result[0] + target.length();
    return result;
  }



  public static boolean isEmail(String email) {
    if (TextUtils.isEmpty(email)) return false;
    Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
    Matcher m = p.matcher(email);
    return m.matches();
  }
}
