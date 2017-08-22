package com.loopeer.codereaderkt.ui.view

abstract class Checker(internal var mCheckObserver: CheckObserver) {
    interface CheckObserver {
        fun check(b: Boolean)
    }

    internal abstract val isEnable: Boolean
}
