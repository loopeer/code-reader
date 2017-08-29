package com.loopeer.codereader.ui.view;

import android.text.TextUtils;

public class AddRepoChecker extends Checker {
    public String repoName;
    public String repoDownloadUrl;

    public AddRepoChecker(CheckObserver checkObserver) {
        super(checkObserver);
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
        mCheckObserver.check(isEnable());
    }

    public void setRepoDownloadUrl(String repoDownloadUrl) {
        this.repoDownloadUrl = repoDownloadUrl;
        mCheckObserver.check(isEnable());
    }

    @Override
    public boolean isEnable() {
        return  !TextUtils.isEmpty(repoDownloadUrl);
    }
}
