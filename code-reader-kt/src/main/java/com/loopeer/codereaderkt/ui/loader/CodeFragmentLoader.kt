package com.loopeer.codereaderkt.ui.loader

import android.view.View
import android.widget.TextView
import android.widget.ViewAnimator
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.ui.view.ProgressIndicatorView


class CodeFragmentContentLoader(contentView: View) : ILoadHelper {

//    @BindView(R.id.progress_code_fragment)
    internal var mProgressIndicatorView: ProgressIndicatorView? = null
//    @BindView(R.id.content_animator)
    internal var mContentAnimator: ViewAnimator? = null
//    @BindView(android.R.id.empty)
    internal var mTextEmpty: TextView? = null

    init {
        mProgressIndicatorView = contentView.findViewById(R.id.progress_code_fragment)
        mContentAnimator = contentView.findViewById(R.id.content_animator)
        mTextEmpty = contentView.findViewById(android.R.id.empty)
//        ButterKnife.bind(this, contentView)
    }

    override fun showProgress() {
        mContentAnimator!!.displayedChild = 1
        mProgressIndicatorView!!.setAnimationStatus(ProgressIndicatorView.AnimStatus.START)
    }

    override fun showContent() {
        mContentAnimator!!.displayedChild = 0
        mProgressIndicatorView!!.setAnimationStatus(ProgressIndicatorView.AnimStatus.CANCEL)
    }

    override fun showEmpty(message: String) {
        mContentAnimator!!.displayedChild = 2
        mTextEmpty!!.text = message
        mProgressIndicatorView!!.setAnimationStatus(ProgressIndicatorView.AnimStatus.CANCEL)
    }
}