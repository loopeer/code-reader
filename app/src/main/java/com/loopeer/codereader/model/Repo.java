package com.loopeer.codereader.model;

import android.text.TextUtils;

public class Repo {

    public String name;
    public long latestOpenTime;
    public String absolutePath;
    public String netUrl;
    public boolean isFolder;

    public boolean isLocal() {
        return !TextUtils.isEmpty(absolutePath);
    }

    public Repo() {
    }
}
