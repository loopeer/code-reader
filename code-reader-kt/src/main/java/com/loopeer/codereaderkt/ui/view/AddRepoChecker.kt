package com.loopeer.codereaderkt.ui.view

import android.text.TextUtils

class AddRepoChecker(checkObserver: Checker.CheckObserver) : Checker(checkObserver) {
    override val isEnable: Boolean
        get() = !TextUtils.isEmpty(repoDownloadUrl) //To change initializer of created properties use File | Settings | File Templates.

    var repoName:String?=null
    set(value) {
        field = value
        mCheckObserver.check(isEnable)
    }
    var repoDownloadUrl: String?=null
    set(value) {
        field = value
        mCheckObserver.check(isEnable)
    }
}
