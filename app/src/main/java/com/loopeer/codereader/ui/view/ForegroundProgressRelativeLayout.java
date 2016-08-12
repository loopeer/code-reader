package com.loopeer.codereader.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.loopeer.codereader.R;

public class ForegroundProgressRelativeLayout extends ForegroundRelativeLayout {

    private Paint mRemainderPaint;
    private Paint mProgressPaint;

    private float mProgress;
    private boolean mIsUnzip;
    private int mRemainderColor;
    private static int sProgressTextPadding;
    private static int sUnzipTextPadding;

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
        mRemainderColor = a.getColor(R.styleable.ForegroundProgressRelativeLayout_remainderColor
                , ContextCompat.getColor(getContext(), R.color.repo_download_remainder_color));
        init();
    }

    private void init() {
        sProgressTextPadding = getResources().getDimensionPixelSize(R.dimen.inline_padding);
        sUnzipTextPadding = getResources().getDimensionPixelSize(R.dimen.medium_padding);

        mRemainderPaint = new Paint();
        mRemainderPaint.setColor(mRemainderColor);
        mRemainderPaint.setStyle(Paint.Style.FILL);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setTextSize(getResources().getDimension(R.dimen.text_size_xxsmall));
    }

    public void setProgress(float i) {
        if (mProgress != i) {
            mProgress = i;
            invalidate();
        }
    }

    public void setUnzip(boolean b) {
        mIsUnzip = b;
        if (mProgress == 1.f) {
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mProgress < 1f) {
            canvas.drawRect(getWidth() * mProgress, 0, getWidth(), getHeight(), mRemainderPaint);
            String content = String.format("%.0f",mProgress * 100) + "%";
            Rect bounds = new Rect();
            mProgressPaint.getTextBounds(content, 0, content.length(), bounds);
            if (getWidth() * (1 - mProgress) > bounds.width()) {
                canvas.drawText(content
                        , getWidth() * mProgress + sProgressTextPadding
                        , getHeight() - sProgressTextPadding
                        , mProgressPaint);
            }
        }

        if (mProgress == 1f && mIsUnzip) {
            String content = getResources().getString(R.string.repo_download_isunzip);
            Rect bounds = new Rect();
            mProgressPaint.getTextBounds(content, 0, content.length(), bounds);
            canvas.drawText(content
                    , getWidth() - sUnzipTextPadding - bounds.width()
                    , getHeight() - sProgressTextPadding
                    , mProgressPaint);
        }
    }
}
