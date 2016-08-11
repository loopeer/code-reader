package com.loopeer.codereader.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.loopeer.codereader.R;

public class ForegroundProgressRelativeLayout extends ForegroundRelativeLayout {

    private Paint mCurrentPaint;
    private Paint mRemainderPaint;

    private float mProgress;

    public ForegroundProgressRelativeLayout(Context context) {
        super(context);
    }

    public ForegroundProgressRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForegroundProgressRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        mCurrentPaint = new Paint();
        mCurrentPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
        mCurrentPaint.setStyle(Paint.Style.FILL);
        mRemainderPaint = new Paint();
        mRemainderPaint.setColor(ContextCompat.getColor(getContext(), R.color.repo_download_remainder_color));
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
