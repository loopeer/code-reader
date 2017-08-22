package com.loopeer.codereaderkt.ui.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View

import com.loopeer.codereaderkt.R

import java.util.ArrayList

class ProgressIndicatorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    internal var scaleFloat1: Float = 0.toFloat()
    internal var scaleFloat2: Float = 0.toFloat()
    internal var degrees: Float = 0.toFloat()

    internal var mIndicatorColor: Int = 0
    internal var mPaint: Paint?=null
    private var mAnimators: List<Animator>? = null
    private var mHasAnimation: Boolean = false

    init {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        mIndicatorColor = ContextCompat.getColor(getContext(), R.color.colorPrimary)
        mPaint = Paint()
        mPaint?.color = mIndicatorColor
        mPaint?.style = Paint.Style.FILL
        mPaint?.isAntiAlias = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setAnimationStatus(AnimStatus.START)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setAnimationStatus(AnimStatus.CANCEL)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measureDimension(dp2px(DEFAULT_SIZE), widthMeasureSpec)
        val height = measureDimension(dp2px(DEFAULT_SIZE), heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun dp2px(dpValue: Int): Int {
        return context.resources.displayMetrics.density.toInt() * dpValue
    }

    private fun measureDimension(defaultSize: Int, measureSpec: Int): Int {
        var result = defaultSize
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)
        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize)
        } else {
            result = defaultSize
        }
        return result
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (!mHasAnimation) {
            mHasAnimation = true
            initAnimation()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val circleSpacing = 12f
        val x = (width / 2).toFloat()
        val y = (height / 2).toFloat()

        canvas.save()
        canvas.translate(x, y)
        canvas.scale(scaleFloat1, scaleFloat1)
        mPaint?.style = Paint.Style.FILL
        canvas.drawCircle(0f, 0f, x / 2.5f, mPaint)

        canvas.restore()

        canvas.translate(x, y)
        canvas.scale(scaleFloat2, scaleFloat2)
        canvas.rotate(degrees)

        mPaint?.strokeWidth = 3f
        mPaint?.style = Paint.Style.STROKE

        val startAngles = floatArrayOf(225f, 45f)
        for (i in 0..1) {
            val rectF = RectF(-x + circleSpacing, -y + circleSpacing, x - circleSpacing, y - circleSpacing)
            canvas.drawArc(rectF, startAngles[i], 90f, false, mPaint)
        }
    }

    fun initAnimation() {
        mAnimators = createAnimation()
    }

    fun setAnimationStatus(animStatus: AnimStatus) {
        if (mAnimators == null) {
            return
        }
        val count = mAnimators!!.size
        for (i in 0..count - 1) {
            val animator = mAnimators!![i]
            val isRunning = animator.isRunning
            when (animStatus) {
                ProgressIndicatorView.AnimStatus.START -> if (!isRunning) {
                    animator.start()
                }
                ProgressIndicatorView.AnimStatus.END -> if (isRunning) {
                    animator.end()
                }
                ProgressIndicatorView.AnimStatus.CANCEL -> if (isRunning) {
                    animator.cancel()
                }
            }
        }
    }

    enum class AnimStatus {
        START, END, CANCEL
    }

    fun createAnimation(): List<Animator> {
        val scaleAnim = ValueAnimator.ofFloat(1f, 0.3f, 1f)
        scaleAnim.duration = 1000
        scaleAnim.repeatCount = -1
        scaleAnim.addUpdateListener { animation ->
            scaleFloat1 = animation.animatedValue as Float
            postInvalidate()
        }
        scaleAnim.start()

        val scaleAnim2 = ValueAnimator.ofFloat(1f, 0.6f, 1f)
        scaleAnim2.duration = 1000
        scaleAnim2.repeatCount = -1
        scaleAnim2.addUpdateListener { animation ->
            scaleFloat2 = animation.animatedValue as Float
            postInvalidate()
        }
        scaleAnim2.start()

        val rotateAnim = ValueAnimator.ofFloat(0f, 180f, 360f)
        rotateAnim.duration = 1000
        rotateAnim.repeatCount = -1
        rotateAnim.addUpdateListener { animation ->
            degrees = animation.animatedValue as Float
            postInvalidate()
        }
        rotateAnim.start()

        val animators = ArrayList<Animator>()
        animators.add(scaleAnim)
        animators.add(scaleAnim2)
        animators.add(rotateAnim)
        return animators
    }

    companion object {
        val DEFAULT_SIZE = 45
    }
}
