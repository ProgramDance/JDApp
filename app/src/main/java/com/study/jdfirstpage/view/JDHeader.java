package com.study.jdfirstpage.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.study.jdfirstpage.R;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by wangheng on 2018/9/26.
 */

public class JDHeader extends RelativeLayout implements PtrUIHandler {
    private static final String TAG = JDHeader.class.getSimpleName();
    private Context mContext;
    private AnimationDrawable mAnimationDrawable;
    private TextView mTvProcess;

    public JDHeader(@NonNull Context context) {
        this(context, null);
    }

    public JDHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JDHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        setGravity(Gravity.CENTER);
        setPadding(0, 20, 0, 20);

        ImageView ivPerson = new ImageView(mContext);
        ivPerson.setId(200);
        ivPerson.setImageResource(R.drawable.frame_animation);
        mAnimationDrawable = (AnimationDrawable) ivPerson.getDrawable();
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(ivPerson, layoutParams);


        mTvProcess = new TextView(mContext);
        mTvProcess.setText("下拉刷新");
        LayoutParams layoutParamsTvProcess = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParamsTvProcess.addRule(RelativeLayout.RIGHT_OF, ivPerson.getId());
        layoutParamsTvProcess.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mTvProcess, layoutParamsTvProcess);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        mTvProcess.setText("下拉刷新");
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        mTvProcess.setText("下拉刷新");
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        mTvProcess.setText("正在刷新");
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        mAnimationDrawable.stop();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        Log.e(TAG, "onUIPositionChange>>STATUS:" + status);
        if (status == PtrFrameLayout.PTR_STATUS_PREPARE) {
            mAnimationDrawable.start();
            if (ptrIndicator.isOverOffsetToRefresh()) {
                mTvProcess.setText("松开刷新");
            } else {
                mTvProcess.setText("下拉刷新");
            }
        }
    }
}
