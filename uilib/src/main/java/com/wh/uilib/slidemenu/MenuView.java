package com.wh.uilib.slidemenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.wh.uilib.R;

/**
 * Created by wangheng on 2018/9/3.
 */

public class MenuView extends ViewGroup {
    private int line;
    private int column;
    private int dividerSize;

    public MenuView(Context context) {
        this(context, null);
    }

    public MenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int childWidth = (widthSpecSize - (column - 1) * dividerSize) / column;

        int childNum = getChildCount();
        for (int i = 0; i < childNum; i++) {
            View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
        }

        if (heightSpecMode != MeasureSpec.EXACTLY) {
            int tempIndex = 0;
            heightSpecSize = 0;
            while (tempIndex < childNum) {
                heightSpecSize += getChildAt(tempIndex).getMeasuredHeight();
                tempIndex += column;
            }
        }

        setMeasuredDimension(widthSpecSize, heightSpecSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int top = 0;

        int childNum = getChildCount();
        for (int i = 0; i < childNum; i++) {
            View child = getChildAt(i);
            child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());

            if ((i + 1) % column == 0) {
                left = 0;
                top += child.getMeasuredHeight() + dividerSize;
            } else {
                left += child.getMeasuredWidth() + dividerSize;
            }
        }
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MenuView);
        line = a.getInt(R.styleable.MenuView_line, 2);
        column = a.getInt(R.styleable.MenuView_column, 4);
        dividerSize = a.getDimensionPixelSize(R.styleable.MenuView_divider_size, 4);
    }
}
