package com.wh.uilib.banner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wangheng on 2018/9/1.
 */

public class IndicatorCircle extends View {
    private float DEFAULT_SIZE = 8.0f;
    private Paint paint;
    private int color = Color.BLACK;

    public IndicatorCircle(Context context) {
        this(context, null);
    }

    public IndicatorCircle(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        paint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode != MeasureSpec.EXACTLY) {
            widthSpecSize = dp2px(DEFAULT_SIZE);
        }

        if (heightSpecMode != MeasureSpec.EXACTLY) {
            heightSpecSize = dp2px(DEFAULT_SIZE);
        }

        setMeasuredDimension(widthSpecSize, heightSpecSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int measureWidth = getMeasuredWidth();
        int measureHeight = getMeasuredHeight();
        float radius = (measureWidth > measureHeight ? measureHeight : measureWidth) / 2.0f;
        paint.setColor(color);
        canvas.drawCircle(radius, radius, radius, paint);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

    private int dp2px(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5);
    }
}
