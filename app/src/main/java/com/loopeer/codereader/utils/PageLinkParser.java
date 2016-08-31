package com.loopeer.codereader.utils;

import android.text.TextUtils;

import retrofit2.Response;

public class PageLinkParser {

    private static final String SPLIT_LINKS = ",";
    private static final String SPLIT_LINK_PARAM = ";";

    private static final String REL_KEY = "rel";

    private static final String REL_VALUE_LAST = "last";
    private static final String REL_VALUE_NEXT = "next";
    private static final String REL_VALUE_FIRST = "first";
    private static final String REL_VALUE_PREV = "prev";

    private int first;
    private int last;
    private int next;
    private int prev;

    private int remain;

    public PageLinkParser(Response response) {
        remain = Integer.parseInt(response.headers().get("X-RateLimit-Remaining"));

        String linkHeader = response.headers().get("Link");
        if (TextUtils.isEmpty(linkHeader))
            return;

        String[] links = linkHeader.split(SPLIT_LINKS);
        for (String link : links) {
            String[] params = link.split(SPLIT_LINK_PARAM);
            if (params.length < 2)
                continue;

            String url = params[0].trim();
            if (!url.startsWith("<") || !url.endsWith(">"))
                continue;
            url = url.substring(1, url.length() - 1);

            for (int i = 1; i < params.length; i++) {
                String[] rel = params[i].trim().split("=");
                if (rel.length < 2 || !REL_KEY.equals(rel[0]))
                    continue;

                String relValue = rel[1];
                if (relValue.startsWith("\"") && relValue.endsWith("\""))
                    relValue = relValue.substring(1, relValue.length() - 1);

                if (REL_VALUE_FIRST.equals(relValue))
                    first = getParam(url);
                else if (REL_VALUE_LAST.equals(relValue))
                    last = getParam(url);
                else if (REL_VALUE_NEXT.equals(relValue))
                    next = getParam(url);
                else if (REL_VALUE_PREV.equals(relValue))
                    prev = getParam(url);
            }
        }
    }

    private int getParam(String url) {
        if (TextUtils.isEmpty(url))
            return 0;
        final String[] params = url.split("&");
        for (String param : params) {
            final String[] parts = param.split("=");
            if (parts.length != 2)
                continue;
            if (!"page".equals(parts[0]))
                continue;
            return Integer.parseInt(parts[1]);
        }
        return 0;
    }

    public int getFirst() {
        return first;
    }

    public int getLast() {
        return last;
    }

    public int getNext() {
        return next;
    }

    public int getPrev() {
        return prev;
    }

    public int getRemain() {
        return remain;
    }
}
