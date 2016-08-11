package com.loopeer.codereader.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.loopeer.codereader.R;

public class ForegroundProgressRelativeLayout extends ForegroundRelativeLayout {

    private Paint mRemainderPaint;

    private float mProgress;
    private int mRemainderColor;

    public ForegroundProgressRelativeLayout(Context context) {
        super(context);
    }

    public ForegroundProgressRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForegroundProgressRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ForegroundProgressRelativeLayout,
                defStyle, 0);
        mRemainderColor = a.getColor(R.styleable.ForegroundProgressRelativeLayout_android_remainderColor
                , ContextCompat.getColor(getContext(), R.color.repo_download_remainder_color));
        init();
    }

    private void init() {
        mRemainderPaint = new Paint();
        mRemainderPaint.setColor(mRemainderColor);
        mRemainderPaint.setStyle(Paint.Style.FILL);
    }

    public void setProgress(float i) {
        mProgress = i;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mProgress < 1f) {
            canvas.drawRect(getWidth() * mProgress, 0, getWidth(), getHeight(), mRemainderPaint);
        }
    }
}
