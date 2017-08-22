package com.loopeer.codereaderkt.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.util.AttributeSet

import com.loopeer.codereaderkt.R

class ForegroundProgressRelativeLayout : ForegroundRelativeLayout {

    private var mRemainderPaint: Paint? = null
    private var mProgressPaint: Paint? = null

    private var mProgressCurrent: Float = 0.toFloat()
    private var mProgressPre: Float = 0.toFloat()
    private var mProgressShow: Float = 0.toFloat()
    private var mIsUnzip: Boolean = false
    private var mRemainderColor: Int=0

    constructor(context: Context) : super(context) {}

    @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) : super(context, attrs, defStyle) {

        val a = getContext().obtainStyledAttributes(attrs, R.styleable.ForegroundProgressRelativeLayout,
            defStyle, 0)
        mRemainderColor = a.getColor(R.styleable.ForegroundProgressRelativeLayout_remainderColor, ContextCompat.getColor(getContext(), R.color.repo_download_remainder_color))
        init()
        setWillNotDraw(false)
    }

    private fun init() {
        sProgressTextPadding = resources.getDimensionPixelSize(R.dimen.inline_padding)
        sUnzipTextPadding = resources.getDimensionPixelSize(R.dimen.medium_padding)

        mRemainderPaint = Paint()
        mRemainderPaint!!.color = mRemainderColor
        mRemainderPaint!!.style = Paint.Style.FILL

        mProgressPaint = Paint()
        mProgressPaint!!.isAntiAlias = true
        mProgressPaint!!.color = ContextCompat.getColor(context, R.color.colorPrimary)
        mProgressPaint!!.style = Paint.Style.FILL
        mProgressPaint!!.textSize = resources.getDimension(R.dimen.text_size_xxsmall)
    }

    fun setProgressCurrent(i: Float) {
        if (mProgressCurrent != i && i != 0f) {
            mProgressPre = mProgressCurrent
            mProgressCurrent = i
            postProgressAnimation()
        } else if (i == 0f) {
            mProgressCurrent = i
            mProgressShow = 0f
            invalidate()
        }
    }

    fun setInitProgress(i: Float) {
        mProgressPre = i
        mProgressCurrent = i
        mProgressShow = i
        invalidate()
    }

    private fun postProgressAnimation() {
        val valueAnimator = ValueAnimator.ofFloat(0F, 1f)
        valueAnimator.addUpdateListener { valueAnimator1 ->
            val fraction = valueAnimator1.animatedFraction
            mProgressShow = mProgressPre + fraction * (mProgressCurrent - mProgressPre)
            invalidate()
        }
        valueAnimator.duration = 500
        valueAnimator.start()
    }

    fun setUnzip(b: Boolean) {
        mIsUnzip = b
        if (mProgressCurrent == 1f) {
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (mProgressCurrent <= 1f) {
            canvas.drawRect(width * mProgressShow, 0f, width.toFloat(), height.toFloat(), mRemainderPaint!!)
            val content = String.format("%.0f", mProgressShow * 100) + "%"
            val bounds = Rect()
            mProgressPaint!!.getTextBounds(content, 0, content.length, bounds)
            if (width * (1 - mProgressShow) > bounds.width()) {
                canvas.drawText(content, width * mProgressShow + sProgressTextPadding, (height - sProgressTextPadding).toFloat(), mProgressPaint!!)
            }
        }

        if (mProgressCurrent == 1f && mIsUnzip) {
            val content = resources.getString(R.string.repo_download_isunzip)
            val bounds = Rect()
            mProgressPaint!!.getTextBounds(content, 0, content.length, bounds)
            canvas.drawText(content, (width - sUnzipTextPadding - bounds.width()).toFloat(), (height - sProgressTextPadding).toFloat(), mProgressPaint!!)
        }
    }

    companion object {
        private var sProgressTextPadding: Int = 0
        private var sUnzipTextPadding: Int = 0
    }
}
