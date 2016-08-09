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

package com.loopeer.codereader.ui.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.loopeer.codereader.R;

public class DividerItemDecorationMainList extends DividerItemDecoration {
    public DividerItemDecorationMainList(Context context) {
        super(context);
    }

    public DividerItemDecorationMainList(Context context, int orientation) {
        super(context, orientation);
    }

    public DividerItemDecorationMainList(Context context, int orientation, int padding, int dividerHeight) {
        super(context, orientation, padding, dividerHeight);
    }

    public DividerItemDecorationMainList(Context context, int orientation, int startpadding, int endpadding, int dividerHeight) {
        super(context, orientation, startpadding, endpadding, dividerHeight);
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount - 1; i++) {
            if (i == 0) {
                continue;
            }
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin +
                    Math.round(ViewCompat.getTranslationY(child));
            final int bottom = top + dividerHeight;

            c.drawRect(left, top, left + startpadding, bottom, mPaddingPaint);
            c.drawRect(right - endpadding, top, right, bottom, mPaddingPaint);
            c.drawRect(left + startpadding, top, right - endpadding, bottom, mDividerPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mOrientation == VERTICAL_LIST) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.set(0, 0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.medium_padding));
            } else if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                outRect.set(0, 0, 0, dividerHeight);
            } else {
                outRect.set(0, 0, 0, 0);
            }
        } else {
            if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                outRect.set(0, 0, dividerHeight, 0);
            } else {
                outRect.set(0, 0, 0, 0);
            }
        }

    }
}
