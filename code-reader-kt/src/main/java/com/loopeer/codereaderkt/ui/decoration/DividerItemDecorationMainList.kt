/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.loopeer.codereaderkt.ui.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.View

import com.loopeer.codereaderkt.R

class DividerItemDecorationMainList : DividerItemDecoration {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, orientation: Int) : super(context, orientation) {}

    constructor(context: Context, orientation: Int, padding: Int, dividerHeight: Int) : super(context, orientation, padding, dividerHeight) {}

    constructor(context: Context, orientation: Int, startpadding: Int, endpadding: Int, dividerHeight: Int) : super(context, orientation, startpadding, endpadding, dividerHeight) {}

    override fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount

        for (i in 0..childCount - 1 - 1) {
            if (i == 0) {
                continue
            }
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin +
                Math.round(ViewCompat.getTranslationY(child))
            val bottom = top + dividerHeight

            c.drawRect(left.toFloat(), top.toFloat(), (left + startpadding).toFloat(), bottom.toFloat(), mPaddingPaint!!)
            c.drawRect((right - endpadding).toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaddingPaint!!)
            c.drawRect((left + startpadding).toFloat(), top.toFloat(), (right - endpadding).toFloat(), bottom.toFloat(), mDividerPaint!!)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        if (mOrientation == DividerItemDecoration.VERTICAL_LIST) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.set(0, 0, 0, mContext.resources.getDimensionPixelSize(R.dimen.medium_padding))
            } else if (parent.getChildAdapterPosition(view) != parent.adapter.itemCount - 1) {
                outRect.set(0, 0, 0, dividerHeight)
            } else {
                outRect.set(0, 0, 0, 0)
            }
        } else {
            if (parent.getChildAdapterPosition(view) != parent.adapter.itemCount - 1) {
                outRect.set(0, 0, dividerHeight, 0)
            } else {
                outRect.set(0, 0, 0, 0)
            }
        }

    }
}
