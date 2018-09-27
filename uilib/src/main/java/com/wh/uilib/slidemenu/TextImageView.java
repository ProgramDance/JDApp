package com.wh.uilib.slidemenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wh.uilib.R;

/**
 * Created by wangheng on 2018/9/3.
 */

public class TextImageView extends LinearLayout {
    private String mText;
    private int mTextSize;
    private int mTextColor;
    private int mImgId;
    private ImageView imageView;
    private TextView textView;

    public TextImageView(Context context) {
        this(context, null);
    }

    public TextImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TextImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
//        setPadding(0, dp2px(2), 0, dp2px(2));

        imageView = new ImageView(getContext(), attrs);
        addView(imageView);

        textView = new TextView(getContext(), attrs);
        textView.setGravity(Gravity.CENTER);
        addView(textView);

        setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d("tag", "onFinishInflate>>");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("tag", "onMeasure>>mode:" + MeasureSpec.getMode(widthMeasureSpec) + ",size:" +
        MeasureSpec.getSize(widthMeasureSpec) + ".." + MeasureSpec.AT_MOST);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d("tag", "onLayout>>");
    }

    public void setImgId(int id){
        imageView.setImageResource(id);
    }

    public void setText(String text){
        textView.setText(text);
    }

    private int dp2px(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5);
    }
}
