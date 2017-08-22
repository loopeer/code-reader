/**
 * Created by YuGang Yang on April 08, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.loopeer.codereaderkt.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet

import com.loopeer.codereaderkt.R

class ForegroundTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : android.support.v7.widget.AppCompatTextView(context, attrs) {


    private var mforeground: Drawable? = null

    init {

        val a = context.obtainStyledAttributes(attrs, R.styleable.ForegroundTextView)
        val foreground = a.getDrawable(R.styleable.ForegroundTextView_android_foreground)
        if (foreground != null) {
            setForeground(foreground)
        }
        a.recycle()
    }

    /**
     * Supply a drawable resource that is to be rendered on top of all of the child
     * views in the frame layout.

     * @param drawableResId The drawable resource to be drawn on top of the children.
     */
    fun setForegroundResource(drawableResId: Int) {
        setForeground(context.resources.getDrawable(drawableResId))
    }

    /**
     * Supply a Drawable that is to be rendered on top of all of the child
     * views in the frame layout.

     * @param drawable The Drawable to be drawn on top of the children.
     */


    override fun setForeground(drawable: Drawable?) {
        if (mforeground === drawable) {
            return
        }
        if (mforeground != null) {
            mforeground!!.callback = null
            unscheduleDrawable(mforeground)
        }

        mforeground = drawable

        if (drawable != null) {
            drawable.callback = this
            if (drawable.isStateful) {
                drawable.state = drawableState
            }
        }
        requestLayout()
        invalidate()
    }

    @SuppressLint("MissingSuperCall")
    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who === mforeground
    }

    @SuppressLint("MissingSuperCall")
    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        if (mforeground != null) mforeground!!.jumpToCurrentState()
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (mforeground != null && mforeground!!.isStateful) {
            mforeground!!.state = drawableState
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mforeground != null) {
            mforeground!!.setBounds(0, 0, measuredWidth, measuredHeight)
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (mforeground != null) {
            mforeground!!.setBounds(0, 0, w, h)
            invalidate()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        if (mforeground != null) {
            mforeground!!.draw(canvas)
        }
    }
}
