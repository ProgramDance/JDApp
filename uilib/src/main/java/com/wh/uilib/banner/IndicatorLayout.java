package com.wh.uilib.banner;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wangheng on 2018/9/1.
 */

public class IndicatorLayout extends ViewGroup {
    private int indicatorNum;

    public IndicatorLayout(Context context) {
        this(context, null);
    }

    public IndicatorLayout(Context context, int indicatorNum) {
        this(context, null);
        this.indicatorNum = indicatorNum;
        initIndicator();
    }

    public IndicatorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init(AttributeSet attrs) {

    }

    private void initIndicator() {
        MarginLayoutParams layoutParams = new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = dp2px(5.0f);
        for (int i = 0; i < indicatorNum; i++) {
            IndicatorCircle indicator = new IndicatorCircle(getContext());
            if (i == 0) {
                indicator.setColor(Color.GRAY);
            }
            addView(indicator, layoutParams);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSzie = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode != MeasureSpec.EXACTLY) {
            widthSpecSize = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
                widthSpecSize += params.leftMargin + params.rightMargin + view.getMeasuredWidth();
            }
        }

        if (heightSpecMode != MeasureSpec.EXACTLY) {
            int tempHeight;
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
                tempHeight = params.topMargin + params.bottomMargin + view.getMeasuredHeight();
                heightSpecSzie = heightSpecSzie > tempHeight ? heightMeasureSpec : tempHeight;
            }
        }

        setMeasuredDimension(widthSpecSize, heightSpecSzie);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int top;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
            left += params.leftMargin;
            top = params.topMargin;
            view.layout(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
            left += view.getMeasuredWidth();
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 设置被选中的item
     *
     * @param position
     */
    public void setSlectItem(int position) {
        for (int i = 0; i < getChildCount(); i++) {
            IndicatorCircle indicator = (IndicatorCircle) getChildAt(i);
            indicator.setColor(i == position ? Color.GRAY : Color.BLACK);
        }
    }

    private int dp2px(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5);
    }
}
