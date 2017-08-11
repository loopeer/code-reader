package com.loopeer.codereaderkt.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.loopeer.codereaderkt.R;

import java.util.ArrayList;
import java.util.List;

public class ProgressIndicatorView extends View {

    float scaleFloat1, scaleFloat2, degrees;
    public static final int DEFAULT_SIZE = 45;

    int mIndicatorColor;
    Paint mPaint;
    private List<Animator> mAnimators;
    private boolean mHasAnimation;

    public ProgressIndicatorView(Context context) {
        this(context, null);
    }

    public ProgressIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mIndicatorColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        mPaint = new Paint();
        mPaint.setColor(mIndicatorColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setAnimationStatus(AnimStatus.START);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setAnimationStatus(AnimStatus.CANCEL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension(dp2px(DEFAULT_SIZE), widthMeasureSpec);
        int height = measureDimension(dp2px(DEFAULT_SIZE), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int dp2px(int dpValue) {
        return (int) getContext().getResources().getDisplayMetrics().density * dpValue;
    }

    private int measureDimension(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!mHasAnimation) {
            mHasAnimation = true;
            initAnimation();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float circleSpacing = 12;
        float x = getWidth() / 2;
        float y = getHeight() / 2;

        canvas.save();
        canvas.translate(x, y);
        canvas.scale(scaleFloat1, scaleFloat1);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0, 0, x / 2.5f, mPaint);

        canvas.restore();

        canvas.translate(x, y);
        canvas.scale(scaleFloat2, scaleFloat2);
        canvas.rotate(degrees);

        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.STROKE);

        float[] startAngles = new float[]{225, 45};
        for (int i = 0; i < 2; i++) {
            RectF rectF = new RectF(-x + circleSpacing, -y + circleSpacing, x - circleSpacing, y - circleSpacing);
            canvas.drawArc(rectF, startAngles[i], 90, false, mPaint);
        }
    }

    public void initAnimation() {
        mAnimators = createAnimation();
    }

    public void setAnimationStatus(AnimStatus animStatus) {
        if (mAnimators == null) {
            return;
        }
        int count = mAnimators.size();
        for (int i = 0; i < count; i++) {
            Animator animator = mAnimators.get(i);
            boolean isRunning = animator.isRunning();
            switch (animStatus) {
                case START:
                    if (!isRunning) {
                        animator.start();
                    }
                    break;
                case END:
                    if (isRunning) {
                        animator.end();
                    }
                    break;
                case CANCEL:
                    if (isRunning) {
                        animator.cancel();
                    }
                    break;
            }
        }
    }

    public enum AnimStatus {
        START, END, CANCEL
    }

    public List<Animator> createAnimation() {
        ValueAnimator scaleAnim = ValueAnimator.ofFloat(1, 0.3f, 1);
        scaleAnim.setDuration(1000);
        scaleAnim.setRepeatCount(-1);
        scaleAnim.addUpdateListener(animation -> {
            scaleFloat1 = (float) animation.getAnimatedValue();
            postInvalidate();
        });
        scaleAnim.start();

        ValueAnimator scaleAnim2 = ValueAnimator.ofFloat(1, 0.6f, 1);
        scaleAnim2.setDuration(1000);
        scaleAnim2.setRepeatCount(-1);
        scaleAnim2.addUpdateListener(animation -> {
            scaleFloat2 = (float) animation.getAnimatedValue();
            postInvalidate();
        });
        scaleAnim2.start();

        ValueAnimator rotateAnim = ValueAnimator.ofFloat(0, 180, 360);
        rotateAnim.setDuration(1000);
        rotateAnim.setRepeatCount(-1);
        rotateAnim.addUpdateListener(animation -> {
            degrees = (float) animation.getAnimatedValue();
            postInvalidate();
        });
        rotateAnim.start();

        List<Animator> animators = new ArrayList<>();
        animators.add(scaleAnim);
        animators.add(scaleAnim2);
        animators.add(rotateAnim);
        return animators;
    }
}
