package com.study.jdfirstpage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;


/**
 * Created by wangheng on 2018/9/26.
 */

public class PullRefreshLayout extends PtrFrameLayout {
    private PtrUIHandler mHead;

    public PullRefreshLayout(Context context) {
        this(context, null);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        setDurationToCloseHeader(500);
        mHead = new JDHeader(getContext());
        setHeaderView((View) mHead);
        addPtrUIHandler(mHead);
    }
}
