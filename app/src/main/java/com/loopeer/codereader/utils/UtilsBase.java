package com.loopeer.codereader.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.style.URLSpan;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.ZoomButtonsController;

import java.util.ArrayList;
import java.util.Arrays;

public class UtilsBase {

    public static <T> ArrayList<T> arrayToList(T[] paramArrayOfT) {
        return new ArrayList(Arrays.asList(paramArrayOfT));
    }

    public static void disableWebviewZoomControls(final WebView paramWebView) {
        paramWebView.getSettings().setSupportZoom(true);
        paramWebView.getSettings().setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT >= 11) {
            new Runnable() {
                @SuppressLint({"NewApi"})
                public void run() {
                    paramWebView.getSettings().setDisplayZoomControls(false);
                }
            }.run();
            return;
        }
        try {
            ((ZoomButtonsController) paramWebView.getClass().getMethod("getZoomButtonsController",
                    new Class[0]).invoke(paramWebView, (Object[]) null)).getContainer().setVisibility(View.GONE);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String domainise(String paramString) {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(Global.URL_DOMAIN);
        localStringBuilder.append(paramString);
        return localStringBuilder.toString();
    }

    public static void fixWebViewJSInterface(WebView paramWebView, Object paramObject, String paramString1, String paramString2) {
        boolean bool = false;
        if (Build.VERSION.RELEASE.startsWith("2.3")) {
            bool = true;
        }
       /* for (; ; ) {
            paramWebView.setWebViewClient(new GingerbreadWebViewClient(paramObject, bool, paramString1, paramString2));
            paramWebView.setWebChromeClient(new GingerbreadWebViewChrome(paramObject, bool, paramString2));
            return;
            paramWebView.addJavascriptInterface(paramObject, paramString1);
        }*/
    }


    public static void makeTextViewHyperlink(TextView paramTextView) {
        SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
        localSpannableStringBuilder.append(paramTextView.getText());
        localSpannableStringBuilder.setSpan(new URLSpan("#"), 0, localSpannableStringBuilder.length(), 33);
        paramTextView.setText(localSpannableStringBuilder, TextView.BufferType.SPANNABLE);
    }

}
