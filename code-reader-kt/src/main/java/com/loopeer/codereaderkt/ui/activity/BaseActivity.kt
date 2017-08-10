package com.loopeer.codereaderkt.ui.activity

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import rx.subscriptions.CompositeSubscription


class BaseActivity : AppCompatActivity() {

    protected var mToolbar: Toolbar? = null
    internal var mCoordinatorContainer: CoordinatorLayout? = null

    private val mAllSubscription = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onContentChanged() {
        super.onContentChanged()
    }


}