package com.loopeer.codereaderkt.ui.view

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.TextView

import com.loopeer.codereaderkt.R

open class ProgressLoading(context: Context, theme: Int) : Dialog(context, theme) {

    private var mProgressView: View? = null
    private var mMessageTextView: TextView? = null

    private var mWindow: Window? = null

    private var mMessage: CharSequence? = null
    private var mShowProgress = true

    init {
        initialize(context, theme)
    }

    private fun initialize(context: Context, theme: Int) {
        mWindow = window
        mWindow!!.requestFeature(Window.FEATURE_NO_TITLE)
        mWindow!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        setContentView(R.layout.progress_loading)
        mMessageTextView = (R.id.text_message) as TextView
        mProgressView = findViewById(R.id.progress)
    }

    fun setMessage(msg: CharSequence): ProgressLoading {
        mMessage = msg
        return this
    }

    fun updateMessage(msg: CharSequence): ProgressLoading {
        mMessage = msg
        show()
        return this
    }

    fun hideMessage(): ProgressLoading {
        return updateMessage("")
    }

    fun hideProgressBar(): ProgressLoading {
        mShowProgress = false
        show()
        return this
    }

    override fun show() {
        if (mMessageTextView != null) {
            if (TextUtils.isEmpty(mMessage)) {
                mMessageTextView!!.visibility = View.GONE
            } else {
                mMessageTextView!!.visibility = View.VISIBLE
                mMessageTextView!!.text = mMessage
            }
        }

        if (mProgressView != null) {
            if (mShowProgress) {
                mProgressView!!.visibility = View.VISIBLE
            } else {
                mProgressView!!.visibility = View.GONE
            }
        }
        super.show()
    }

}