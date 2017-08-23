package com.loopeer.codereaderkt.ui.view

import android.text.TextUtils

class LoginChecker(checkObserver: Checker.CheckObserver) : Checker(checkObserver) {

    var username: String?=null
    set(value) {
        field=value
        mCheckObserver.check(isEnable)
    }
    var password: String?=null
    set(value) {
        field=value
        mCheckObserver.check(isEnable)
    }


     override val isEnable: Boolean
        get() = !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)
}
