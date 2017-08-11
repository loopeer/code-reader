package com.loopeer.codereaderkt.ui.loader

import android.widget.ViewAnimator


class RecyclerLoader(internal var mViewAnimator: ViewAnimator) : ILoadHelper {

    override fun showProgress() {
        mViewAnimator.displayedChild = 2
    }

    override fun showContent() {
        mViewAnimator.displayedChild = 0
    }

    override fun showEmpty(message: String) {
        mViewAnimator.displayedChild = 1
    }
}