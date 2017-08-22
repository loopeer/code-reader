/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.loopeer.codereaderkt.ui.view

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.widget.RelativeLayout

import com.loopeer.codereaderkt.R

open class ForegroundRelativeLayout : RelativeLayout {

    private var mForeground: Drawable? = null

    private val mSelfBounds = Rect()
    private val mOverlayBounds = Rect()

    private var mForegroundGravity = Gravity.FILL

    protected var mForegroundInPadding = true

    internal var mForegroundBoundsChanged = false

    constructor(context: Context) : super(context) {}

    @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) : super(context, attrs, defStyle) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.ForegroundRelativeLayout,
            defStyle, 0)

        mForegroundGravity = a.getInt(
            R.styleable.ForegroundRelativeLayout_android_foregroundGravity, mForegroundGravity)

        val d = a.getDrawable(R.styleable.ForegroundRelativeLayout_android_foreground)
        if (d != null) {
            foreground = d
        }

        mForegroundInPadding = a.getBoolean(
            R.styleable.ForegroundRelativeLayout_android_foregroundInsidePadding, true)

        a.recycle()
    }

    /**
     * Describes how the foreground is positioned.

     * @return foreground gravity.
     * *
     * @see .setForegroundGravity
     */
    override fun getForegroundGravity(): Int {
        return mForegroundGravity
    }

    /**
     * Describes how the foreground is positioned. Defaults to START and TOP.

     * @param foregroundGravity See [Gravity]
     * *
     * @see .getForegroundGravity
     */
    override fun setForegroundGravity(foregroundGravity: Int) {
        var foregroundGravity = foregroundGravity
        if (mForegroundGravity != foregroundGravity) {
            if (foregroundGravity and Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK == 0) {
                foregroundGravity = foregroundGravity or Gravity.START
            }

            if (foregroundGravity and Gravity.VERTICAL_GRAVITY_MASK == 0) {
                foregroundGravity = foregroundGravity or Gravity.TOP
            }

            mForegroundGravity = foregroundGravity


            if (mForegroundGravity == Gravity.FILL && mForeground != null) {
                val padding = Rect()
                mForeground!!.getPadding(padding)
            }

            requestLayout()
        }
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || who === mForeground
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()
        if (mForeground != null) mForeground!!.jumpToCurrentState()
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (mForeground != null && mForeground!!.isStateful) {
            mForeground!!.state = drawableState
        }
    }

    /**
     * Supply a Drawable that is to be rendered on top of all of the child
     * views in the frame layout.  Any padding in the Drawable will be taken
     * into account by ensuring that the children are inset to be placed
     * inside of the padding area.

     * @param drawable The Drawable to be drawn on top of the children.
     */
    override fun setForeground(drawable: Drawable?) {
        if (mForeground !== drawable) {
            if (mForeground != null) {
                mForeground!!.callback = null
                unscheduleDrawable(mForeground)
            }

            mForeground = drawable

            if (drawable != null) {
                setWillNotDraw(false)
                drawable.callback = this
                if (drawable.isStateful) {
                    drawable.state = drawableState
                }
                if (mForegroundGravity == Gravity.FILL) {
                    val padding = Rect()
                    drawable.getPadding(padding)
                }
            } else {
                setWillNotDraw(true)
            }
            requestLayout()
            invalidate()
        }
    }

    /**
     * Returns the drawable used as the foreground of this FrameLayout. The
     * foreground drawable, if non-null, is always drawn on top of the children.

     * @return A Drawable or null if no foreground was set.
     */
    override fun getForeground(): Drawable? {
        return mForeground
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed)
            mForegroundBoundsChanged = changed
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mForegroundBoundsChanged = true
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        if (mForeground != null) {
            val foreground = mForeground

            if (mForegroundBoundsChanged) {
                mForegroundBoundsChanged = false
                val selfBounds = mSelfBounds
                val overlayBounds = mOverlayBounds

                val w = right - left
                val h = bottom - top

                if (mForegroundInPadding) {
                    selfBounds.set(0, 0, w, h)
                } else {
                    selfBounds.set(paddingLeft, paddingTop,
                        w - paddingRight, h - paddingBottom)
                }

                Gravity.apply(mForegroundGravity, foreground!!.intrinsicWidth,
                    foreground.intrinsicHeight, selfBounds, overlayBounds)
                foreground.bounds = overlayBounds
            }

            foreground!!.draw(canvas)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun drawableHotspotChanged(x: Float, y: Float) {
        super.drawableHotspotChanged(x, y)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mForeground != null) {
                mForeground!!.setHotspot(x, y)
            }
        }
    }
}
