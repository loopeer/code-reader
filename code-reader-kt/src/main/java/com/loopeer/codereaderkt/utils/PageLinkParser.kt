package com.loopeer.codereaderkt.utils

import android.text.TextUtils
import retrofit2.Response


class PageLinkParser(response: Response<*>) {

    var first: Int = 0
        private set
    var last: Int = 0
        private set
    var next: Int = 0
        private set
    var prev: Int = 0
        private set

    val remain: Int = Integer.parseInt(response.headers().get("X-RateLimit-Remaining"))

    init {

        val linkHeader = response.headers().get("Link")
        if (TextUtils.isEmpty(linkHeader)){}
//            return

        val links = linkHeader.split(SPLIT_LINKS.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (link in links) {
            val params = link.split(SPLIT_LINK_PARAM.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (params.size < 2)
                continue

            var url = params[0].trim { it <= ' ' }
            if (!url.startsWith("<") || !url.endsWith(">"))
                continue
            url = url.substring(1, url.length - 1)

            for (i in 1..params.size - 1) {
                val rel = params[i].trim { it <= ' ' }.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (rel.size < 2 || REL_KEY != rel[0])
                    continue

                var relValue = rel[1]
                if (relValue.startsWith("\"") && relValue.endsWith("\""))
                    relValue = relValue.substring(1, relValue.length - 1)

                if (REL_VALUE_FIRST == relValue)
                    first = getParam(url)
                else if (REL_VALUE_LAST == relValue)
                    last = getParam(url)
                else if (REL_VALUE_NEXT == relValue)
                    next = getParam(url)
                else if (REL_VALUE_PREV == relValue)
                    prev = getParam(url)
            }
        }
    }

    private fun getParam(url: String): Int {
        if (TextUtils.isEmpty(url))
            return 0
        val params = url.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (param in params) {
            val parts = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size != 2)
                continue
            if ("page" != parts[0])
                continue
            return Integer.parseInt(parts[1])
        }
        return 0
    }

    companion object {

        private val SPLIT_LINKS = ","
        private val SPLIT_LINK_PARAM = ";"

        private val REL_KEY = "rel"

        private val REL_VALUE_LAST = "last"
        private val REL_VALUE_NEXT = "next"
        private val REL_VALUE_FIRST = "first"
        private val REL_VALUE_PREV = "prev"
    }
}