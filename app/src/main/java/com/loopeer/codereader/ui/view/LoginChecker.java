package com.loopeer.codereader.ui.view;

import android.text.TextUtils;

public class LoginChecker extends Checker {

    public String username;
    public String password;

    public LoginChecker(CheckObserver checkObserver) {
        super(checkObserver);
    }

    public void setUsername(String username) {
        this.username = username;
        mCheckObserver.check(isEnable());
    }

    public void setPassword(String password) {
        this.password = password;
        mCheckObserver.check(isEnable());
    }

    @Override
    boolean isEnable() {
        return !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password);
    }
}
