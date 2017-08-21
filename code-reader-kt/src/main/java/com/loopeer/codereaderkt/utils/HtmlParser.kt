package com.loopeer.codereaderkt.utils

import android.content.Context
import android.os.Build
import com.loopeer.codereaderkt.R
import java.io.IOException


object HtmlParser {
    fun buildHtmlContent(context: Context, paramString1: String, jsFile: String, fileName: String): String {
        var jsFile = jsFile
        while (true) {
            try {
                val inputStream = context.assets.open("code.html")
                var localObject: Any = ByteArray(inputStream.available())
                inputStream.read(localObject as ByteArray)
                inputStream.close()
                localObject = String(localObject)
                val localStringBuilder = StringBuilder()
                localStringBuilder.append("SyntaxHighlighter.defaults['auto-links'] = false;")
                localStringBuilder.append("SyntaxHighlighter.defaults['toolbar'] = false;")
                localStringBuilder.append("SyntaxHighlighter.defaults['wrap-lines'] = false;")
                localStringBuilder.append("SyntaxHighlighter.defaults['quick-code'] = false;")
                if (!PrefUtils.getPrefDisplayLineNumber(context)) {
                    localStringBuilder.append("SyntaxHighlighter.defaults['gutter'] = false;")
                }
                localStringBuilder.append("SyntaxHighlighter.all();")
                var temp = ""
                if (Build.VERSION.SDK_INT < 14) {
                    temp = "$('.syntaxhighlighter').css('overflow', 'visible !important');"
                }
                jsFile = localObject
                        .replace("!FONT_SIZE!", String.format("<style>.code .syntaxhighlighter { font-size: %.2fpx !important; }</style>", *arrayOf<Any>(java.lang.Float.valueOf(PrefUtils.getPrefFontSize(context)))))
                        .replace("!FILENAME!", fileName)
                        .replace("!BRUSHJSFILE!", jsFile)
                        .replace("!SYNTAXHIGHLIGHTER!", localStringBuilder.toString())
                        .replace("!JS_FIX_HSCROLL!", temp)
                temp = "<link type='text/css' rel='stylesheet' href='style_menlo.css'/>"
                return jsFile
                        .replace("!STYLE_MENLO!", if (PrefUtils.getPrefMenlofont(context)) temp else "")
                        .replace("!THEME!", PrefUtils.getPrefTheme(context))
                        .replace("!CODE!", paramString1)

                        .replace("!WINDOW_BACK_GROUND_COLOR!", ColorUtils.getColorString(context, R.color.code_read_background_color))
            } catch (e: IOException) {
                throw RuntimeException(e)
            } finally {

            }
        }
    }
}