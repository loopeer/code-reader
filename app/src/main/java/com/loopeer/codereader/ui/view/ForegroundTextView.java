/**
 * Created by YuGang Yang on April 08, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.loopeer.codereader.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.loopeer.codereader.R;

public class ForegroundTextView extends TextView {
  private Drawable foreground;

  public ForegroundTextView(Context context) {
    this(context, null);
  }

  public ForegroundTextView(Context context, AttributeSet attrs) {
    super(context, attrs);

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ForegroundTextView);
    Drawable foreground = a.getDrawable(R.styleable.ForegroundTextView_android_foreground);
    if (foreground != null) {
      setForeground(foreground);
    }
    a.recycle();
  }

  /**
   * Supply a drawable resource that is to be rendered on top of all of the child
   * views in the frame layout.
   *
   * @param drawableResId The drawable resource to be drawn on top of the children.
   */
  public void setForegroundResource(int drawableResId) {
    setForeground(getContext().getResources().getDrawable(drawableResId));
  }

  /**
   * Supply a Drawable that is to be rendered on top of all of the child
   * views in the frame layout.
   *
   * @param drawable The Drawable to be drawn on top of the children.
   */
  public void setForeground(Drawable drawable) {
    if (foreground == drawable) {
      return;
    }
    if (foreground != null) {
      foreground.setCallback(null);
      unscheduleDrawable(foreground);
    }

    foreground = drawable;

    if (drawable != null) {
      drawable.setCallback(this);
      if (drawable.isStateful()) {
        drawable.setState(getDrawableState());
      }
    }
    requestLayout();
    invalidate();
  }

  @Override protected boolean verifyDrawable(Drawable who) {
    return super.verifyDrawable(who) || who == foreground;
  }

  @Override public void jumpDrawablesToCurrentState() {
    super.jumpDrawablesToCurrentState();
    if (foreground != null) foreground.jumpToCurrentState();
  }

  @Override protected void drawableStateChanged() {
    super.drawableStateChanged();
    if (foreground != null && foreground.isStateful()) {
      foreground.setState(getDrawableState());
    }
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    if (foreground != null) {
      foreground.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
      invalidate();
    }
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    if (foreground != null) {
      foreground.setBounds(0, 0, w, h);
      invalidate();
    }
  }

  @Override public void draw(Canvas canvas) {
    super.draw(canvas);

    if (foreground != null) {
      foreground.draw(canvas);
    }
  }
}
