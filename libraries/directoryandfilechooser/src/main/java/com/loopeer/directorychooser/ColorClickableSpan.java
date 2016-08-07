package com.loopeer.directorychooser;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;

public abstract class ColorClickableSpan extends ClickableSpan {
  public int mColorRes;
  private Context mContext;

  public ColorClickableSpan(Context context, int color) {
    mColorRes = color;
    mContext = context;
  }

  @Override public void updateDrawState(TextPaint tp) {
    tp.setColor(ContextCompat.getColor(mContext, mColorRes));
    tp.setUnderlineText(false);
  }
}
