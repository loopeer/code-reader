package com.loopeer.codereaderkt.ui.view


abstract class Checker() {
    interface CheckObserver {
        fun check(b: Boolean)
    }

    internal lateinit var mCheckObserver: CheckObserver

    constructor(checkObserver: CheckObserver) : this() {
        this.mCheckObserver = checkObserver
    }

    internal abstract val isEnable: Boolean
}