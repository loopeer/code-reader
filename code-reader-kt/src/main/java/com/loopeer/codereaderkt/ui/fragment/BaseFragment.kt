package com.loopeer.codereaderkt.ui.fragment

import android.support.v4.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.ui.view.ProgressLoading
import rx.Subscription
import rx.subscriptions.CompositeSubscription


open class BaseFragment : Fragment() {

    val mAllSubscribe: CompositeSubscription = CompositeSubscription()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    fun registerSubscription(subscription: Subscription) {
        mAllSubscribe.add(subscription)
    }

    fun unregisterSubscription(subscription: Subscription) {
        mAllSubscribe.remove(subscription)
    }

    fun clearSubscription() {
        mAllSubscribe.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearSubscription()
        if (isProgressShow() && mProgressLoading == null) {
            dismissProgressLoading()
            mProgressLoading = null
        }
    }

    private var mProgressLoading: ProgressLoading? = null
    private var mUnBackProgressLoading: ProgressLoading? = null
    private var progressShow: Boolean = false

    fun getProgressLoaing(): ProgressLoading? = mProgressLoading

    fun showProgressLoading(resId: Int): Unit {
        showProgressLoading(getString(resId))
    }

    open fun showProgressLoading(message: String) {
        if (mProgressLoading == null) {
            mProgressLoading = ProgressLoading(activity, R.style.ProgressLoadingTheme)
            mProgressLoading!!.setCanceledOnTouchOutside(true)
            mProgressLoading!!.setOnCancelListener { progressShow = false }
        }
        if (!TextUtils.isEmpty(message)) {
            mProgressLoading!!.setMessage(message)
        } else {
            mProgressLoading!!.setMessage("")
        }
        progressShow = true
        mProgressLoading!!.show()
    }

    fun isProgressShow(): Boolean = progressShow

    open fun dismissProgressLoading() {
        if (mProgressLoading != null && isVisible) {
            progressShow = false
            mProgressLoading!!.dismiss()
        }
    }

    fun showUnBackProgressLoading(resId: Int) {
        showUnBackProgressLoading(getString(resId))
    }

    fun showUnBackProgressLoading(message: String) {
        if (mUnBackProgressLoading == null) {
            mUnBackProgressLoading = object : ProgressLoading(activity, R.style.ProgressLoadingTheme) {
                override fun onBackPressed() {
                    super.onBackPressed()
                }
            }
        }
        if (!TextUtils.isEmpty(message)) {
            mUnBackProgressLoading!!.setMessage(message)
        } else {
            mUnBackProgressLoading!!.setMessage("")
        }
        mUnBackProgressLoading!!.show()
    }

    fun dismissUnBackProgressLoading() {
        if (mUnBackProgressLoading != null && isVisible) {
            mUnBackProgressLoading!!.dismiss()
        }
    }

}











