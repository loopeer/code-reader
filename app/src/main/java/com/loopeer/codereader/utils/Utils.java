package com.loopeer.codereader.utils;

import android.content.Context;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;

public class Utils extends UtilsBase {
    public static String buildHtmlContent(Context paramContext, String paramString1, String jsFile, String fileName) {
        for (; ; ) {
            try {
                InputStream inputStream = paramContext.getAssets().open("code.html");
                Object localObject = new byte[inputStream.available()];
                inputStream.read((byte[]) localObject);
                inputStream.close();
                localObject = new String((byte[]) localObject);
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append("SyntaxHighlighter.defaults['auto-links'] = false;");
                localStringBuilder.append("SyntaxHighlighter.defaults['toolbar'] = false;");
                localStringBuilder.append("SyntaxHighlighter.defaults['wrap-lines'] = false;");
                localStringBuilder.append("SyntaxHighlighter.defaults['quick-code'] = false;");
                if (!G.settings.displayLineNumbers) {
                    localStringBuilder.append("SyntaxHighlighter.defaults['gutter'] = false;");
                }
                localStringBuilder.append("SyntaxHighlighter.all();");
                String temp = "";
                if (Build.VERSION.SDK_INT < 14) {
                    temp = "$('.syntaxhighlighter').css('overflow', 'visible !important');";
                }
                jsFile = ((String) localObject).replace("!FONT_SIZE!"
                        , String.format("<style>.code .syntaxhighlighter { font-size: %.2fpx !important; }</style>"
                                , new Object[]{Float.valueOf(G.settings.fontSize)})).replace("!FILENAME!"
                        , fileName).replace("!BRUSHJSFILE!", jsFile).replace("!SYNTAXHIGHLIGHTER!"
                        , localStringBuilder.toString()).replace("!JS_FIX_HSCROLL!", temp);
                if (G.settings.monospaceFont) {
                    temp = "<link type='text/css' rel='stylesheet' href='style_monospace.css'/>";
                    return jsFile.replace("!STYLE_MONOSPACE!", temp).replace("!THEME!", G.settings.theme).replace("!CODE!", paramString1);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {

            }
        }
    }
}
