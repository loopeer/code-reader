package com.loopeer.codereaderkt.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.event.DownloadRepoMessageEvent
import com.loopeer.codereaderkt.event.ThemeRecreateEvent
import com.loopeer.codereaderkt.ui.view.ProgressLoading
import com.loopeer.codereaderkt.utils.RxBus
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription


open class BaseActivity : AppCompatActivity() {

    internal var mToolbar: Toolbar? = null
    private var mCoordinatorContainer: CoordinatorLayout? = null


    private val mAllSubscription = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerSubscription(
                RxBus.getInstance()
                        .toObservable()
                        .filter({ o -> o is DownloadRepoMessageEvent })
                        .map({ o -> o as DownloadRepoMessageEvent })
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext({ o -> showMessage(o.message) })
                        .subscribe())

        registerSubscription(
                RxBus.getInstance()
                        .toObservable()
                        .filter({ o -> o is ThemeRecreateEvent })
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext({ o -> recreate() })
                        .subscribe())
        //打开app恢复下载
    }

    override fun onContentChanged() {
        super.onContentChanged()
        mToolbar = findViewById(R.id.toolbar) as Toolbar?
        mCoordinatorContainer = findViewById(R.id.view_coordinator_container) as CoordinatorLayout?
        if (mToolbar != null) {
            setSupportActionBar(mToolbar)
            onSetupActionBar(this.supportActionBar!!)
        }

        val title = intent.getStringExtra(Intent.EXTRA_TITLE)
        if (!TextUtils.isEmpty(title)) {
            setTitle(title)
        }

    }

    private fun onSetupActionBar(actionBar: ActionBar) {
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    protected fun registerSubscription(subscription: Subscription) {
        mAllSubscription.add(subscription)

    }

    protected fun unregisterSubscription(subscription: Subscription) {
        mAllSubscription.remove(subscription)
    }

    private fun clearSubscription() {
        mAllSubscription.clear()
    }

    protected open fun reCreateRefresh() {

    }

    override fun onDestroy() {
        super.onDestroy()
        clearSubscription()
        if (isProgressShow() && mProgressLoading != null) {
            dismissProgressLoading()
            mProgressLoading = null
        }
    }

    private var mProgressLoading: ProgressLoading? = null
    private lateinit var mUnBackProgressLoading: ProgressLoading
    private var progressShow: Boolean = false

    fun showProgressLoading(resId: Int) {
        showProgressLoading(getString(resId))
    }

    fun showProgressLoading(message: String) {
        if (mProgressLoading == null) {
            mProgressLoading = ProgressLoading(this, R.style.ProgressLoadingTheme)
            mProgressLoading!!.setCanceledOnTouchOutside(true)
            mProgressLoading!!.setOnCancelListener({ progressShow = false })
        }
        if (!TextUtils.isEmpty(message)) {
            mProgressLoading!!.setMessage(message)
        } else {
            mProgressLoading!!.setMessage(null)
        }
        progressShow = true
        mProgressLoading!!.show()
    }

    private fun isProgressShow(): Boolean = progressShow

    fun dismissProgressLoading() {
        if (mProgressLoading != null && !isFinishing) {
            progressShow = false
            mProgressLoading!!.dismiss()
        }
    }

    fun showUnBackProgressLoading(resId: Int) {
        showUnBackProgressLoading(getString(resId))
    }

    // 按返回键不可撤销的
    private fun showUnBackProgressLoading(message: String) {
        if (!TextUtils.isEmpty(message)) {
            mUnBackProgressLoading.setMessage(message)
        } else {
            mUnBackProgressLoading.setMessage(null)
        }
        mUnBackProgressLoading.show()
    }

    fun dismissUnBackProgressLoading() {
        if (!isFinishing) {
            mUnBackProgressLoading.dismiss()
        }
    }

    fun hideSoftInputMethod() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun showMessage(message: String) {
        if (mCoordinatorContainer != null)
            Snackbar.make(mCoordinatorContainer!!, message, Snackbar.LENGTH_SHORT).show()
    }


}