package com.loopeer.codereaderkt.ui.fragment

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View


open class BaseFullscreenFragment : BaseFragment() {
    private val AUTO_HIDE = true
    private val AUTO_HIDE_DELAY_MILLIS = 3000
    private val UI_ANIMATION_DELAY = 300

    private var mDecorView: View? = null

    private var mHideHandler = android.os.Handler()

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private final var mHideRunnable = Runnable() {
        ->
        run {
            mDecorView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

    }

    private var mVisible: Boolean = false

    protected val mDelayHideTouchListener = { ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mDecorView = activity.window.decorView
        mVisible = true
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    fun hide() {
        mVisible = false
        mHideHandler.postDelayed(mHideRunnable, UI_ANIMATION_DELAY.toLong())
    }

    protected fun show() {
        mDecorView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        mHideHandler.removeCallbacks(mHideRunnable)
    }

    protected fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }
}