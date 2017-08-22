package com.loopeer.codereaderkt.ui.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.webkit.WebView
import android.widget.AbsoluteLayout
import android.widget.ProgressBar

import com.loopeer.codereaderkt.R

class ProgressWebView(context: Context, attrs: AttributeSet) : WebView(context, attrs) {

    private val mProgressBar: ProgressBar

    init {
        mProgressBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
        mProgressBar.isIndeterminate = false
        mProgressBar.progressDrawable = ContextCompat.getDrawable(context, R.drawable.progress_horizontal_web_view)
        mProgressBar.indeterminateDrawable = ContextCompat.getDrawable(context, android.R.drawable.progress_indeterminate_horizontal)
        mProgressBar.layoutParams = AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT, 5, 0, 0)
        mProgressBar.minimumHeight = 16
        addView(mProgressBar)
        webChromeClient = WebChromeClient()
    }

    fun setProgressbarGone() {
        mProgressBar.visibility = View.GONE
    }

    inner class WebChromeClient : android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (newProgress == 100) {
                mProgressBar.visibility = View.GONE
            } else {
                if (mProgressBar.visibility == View.GONE) mProgressBar.visibility = View.VISIBLE
                mProgressBar.progress = newProgress
            }
            super.onProgressChanged(view, newProgress)
        }

    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        val lp = mProgressBar.layoutParams as AbsoluteLayout.LayoutParams
        lp.x = l
        lp.y = t
        mProgressBar.layoutParams = lp
        super.onScrollChanged(l, t, oldl, oldt)
    }
}