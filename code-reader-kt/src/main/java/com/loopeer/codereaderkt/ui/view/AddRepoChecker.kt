package com.loopeer.codereaderkt.ui.view

import android.text.TextUtils


class AddRepoChecker() : Checker() {
    override val isEnable: Boolean
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    private lateinit var repoName: String
    private lateinit var repoDownloadUrl: String

    constructor(checkObserver: CheckObserver) : this() {

    }

    fun setRepoName(repoName: String) {
        this.repoName = repoName
        mCheckObserver.check(isEnable())
    }

    fun setRepoDownloadUrl(repoDownloadUrl: String) {
        this.repoDownloadUrl = repoDownloadUrl
        mCheckObserver.check(isEnable())
    }

    fun isEnable(): Boolean {
        return !TextUtils.isEmpty(repoName) && !TextUtils.isEmpty(repoDownloadUrl)
    }
}