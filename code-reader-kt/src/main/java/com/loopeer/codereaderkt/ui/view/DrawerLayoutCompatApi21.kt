package com.loopeer.codereaderkt.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets

internal object DrawerLayoutCompatApi21 {

    private val THEME_ATTRS = intArrayOf(android.R.attr.colorPrimaryDark)

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    fun configureApplyInsets(drawerLayout: View) {
        if (drawerLayout is DrawerLayoutImpl) {
            drawerLayout.setOnApplyWindowInsetsListener(InsetsListener())
            drawerLayout.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    fun dispatchChildInsets(child: View, insets: Any, gravity: Int) {
        var wi = insets as WindowInsets
        if (gravity == Gravity.LEFT) {
            wi = wi.replaceSystemWindowInsets(wi.systemWindowInsetLeft,
                wi.systemWindowInsetTop, 0, wi.systemWindowInsetBottom)
        } else if (gravity == Gravity.RIGHT) {
            wi = wi.replaceSystemWindowInsets(0, wi.systemWindowInsetTop,
                wi.systemWindowInsetRight, wi.systemWindowInsetBottom)
        }
        child.dispatchApplyWindowInsets(wi)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    fun applyMarginInsets(lp: ViewGroup.MarginLayoutParams, insets: Any,
                          gravity: Int) {
        var wi = insets as WindowInsets
        if (gravity == Gravity.LEFT) {
            wi = wi.replaceSystemWindowInsets(wi.systemWindowInsetLeft,
                wi.systemWindowInsetTop, 0, wi.systemWindowInsetBottom)
        } else if (gravity == Gravity.RIGHT) {
            wi = wi.replaceSystemWindowInsets(0, wi.systemWindowInsetTop,
                wi.systemWindowInsetRight, wi.systemWindowInsetBottom)
        }
        lp.leftMargin = wi.systemWindowInsetLeft
        lp.topMargin = wi.systemWindowInsetTop
        lp.rightMargin = wi.systemWindowInsetRight
        lp.bottomMargin = wi.systemWindowInsetBottom
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    fun getTopInset(insets: Any?): Int {
        return if (insets != null) (insets as WindowInsets).systemWindowInsetTop else 0
    }

    fun getDefaultStatusBarBackground(context: Context): Drawable {
        val a = context.obtainStyledAttributes(THEME_ATTRS)
        try {
            return a.getDrawable(0)
        } finally {
            a.recycle()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    internal class InsetsListener : View.OnApplyWindowInsetsListener {
        override fun onApplyWindowInsets(v: View, insets: WindowInsets): WindowInsets {
            val drawerLayout = v as DrawerLayoutImpl
            drawerLayout.setChildInsets(insets, insets.systemWindowInsetTop > 0)
            return insets.consumeSystemWindowInsets()
        }
    }
}