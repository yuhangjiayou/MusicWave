package com.zyhang.musicWave;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * ProjectName:MusicWave
 * Description:
 * Created by zyhang on 2017/7/11.下午10:17
 * Modify by:
 * Modify time:
 * Modify remark:
 */

public class MusicWaveView extends View {

    private int mCount = 5;
    private int mColor = Color.RED;
    private String mDelays = "200 500 100 400 300";
    private float mRadius = 5;
    private int mDuration = 1000;

    private RectF mRectF;
    private Paint mPaint;
    private float[] mFloats;
    private ArrayList<ValueAnimator> mAnimators;

    public MusicWaveView(Context context) {
        this(context, null);
    }

    public MusicWaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MusicWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

        if (attrs != null) {
            final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MusicWaveView);
            mCount = ta.getInteger(R.styleable.MusicWaveView_mwv_count, mCount);
            mColor = ta.getColor(R.styleable.MusicWaveView_mwv_color, mColor);
            if (ta.hasValue(R.styleable.MusicWaveView_mwv_delays)) {
                mDelays = ta.getString(R.styleable.MusicWaveView_mwv_delays);
            }
            mRadius = ta.getDimension(R.styleable.MusicWaveView_mwv_radius, mRadius);
            mDuration = ta.getInteger(R.styleable.MusicWaveView_mwv_duration, mDuration);
            ta.recycle();
        }

        mRectF = new RectF();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mColor);
        mFloats = new float[mCount];
        mAnimators = new ArrayList<>();
        String[] delays = mDelays.split(" ");
        for (int i = 0; i < mCount; i++) {
            final int index = i;
            mFloats[index] = 1.0F;
            ValueAnimator animator = ValueAnimator.ofFloat(1.0F, -0.5F, 1F);
            animator.setDuration(mDuration);
            animator.setRepeatCount(-1);
            animator.setStartDelay(Long.parseLong(delays[index]));
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mFloats[index] = (float) valueAnimator.getAnimatedValue();
                    if (index == 0) {
                        invalidate();
                    }
                }
            });
            mAnimators.add(animator);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float translateX = getWidth() / (2 * mCount - 1);
        float translateY = getHeight() / 2;

        for (int i = 0; i < mCount; i++) {
            canvas.save();
            canvas.translate((1 + i * 2) * translateX - translateX / 2, translateY);
            mRectF.set(-translateX / 2, -translateY * mFloats[i], translateX / 2, translateY);
            canvas.drawRoundRect(mRectF, mRadius, mRadius, mPaint);
            canvas.restore();
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            start();
        } else {
            stop();
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            start();
        } else {
            stop();
        }
    }

    public void start() {
        if (getVisibility() != VISIBLE || mAnimators == null) {
            stop();
            return;
        }
        for (ValueAnimator animator : mAnimators) {
            animator.start();
        }
    }

    public void stop() {
        if (mAnimators == null) {
            return;
        }
        for (ValueAnimator animator : mAnimators) {
            animator.end();
        }
    }
}
