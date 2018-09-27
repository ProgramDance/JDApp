package com.wh.uilib.banner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by wangheng on 2018/8/31.
 */

public class Banner extends RelativeLayout {
    private static final String TAG = Banner.class.getSimpleName();
    private ViewPager viewPager;
    private IndicatorLayout indicatorLayout;
    private List<Integer> imgs;
    private long bannerCycle = 5; // 单位秒
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            handler.sendEmptyMessageDelayed(0, bannerCycle * 1000);
        }
    };

    public Banner(Context context) {
        this(context, null);
    }

    public Banner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Banner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        viewPager = new ViewPager(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        viewPager.setLayoutParams(layoutParams);
        addView(viewPager);
    }

    public void setContent(final List<Integer> imgs) {
        this.imgs = imgs;
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = new ImageView(getContext());
                imageView.setImageResource(imgs.get(position % imgs.size()));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(TAG, "onPageScrolled>>");
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected>>");
                indicatorLayout.setSlectItem(position % imgs.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d(TAG, "onPageScrollStateChanged>>");
            }
        });

        indicatorLayout = new IndicatorLayout(getContext(), imgs.size());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.setMargins(0, 0, 0, dp2px(2));
        indicatorLayout.setLayoutParams(layoutParams);
        addView(indicatorLayout);

        int IntMaxHalf = Integer.MAX_VALUE / 2;
        int startItemPosition = IntMaxHalf - IntMaxHalf % imgs.size();
        viewPager.setCurrentItem(startItemPosition);
        handler.removeMessages(0);
        handler.sendEmptyMessageDelayed(0, bannerCycle * 1000);
    }

    private int dp2px(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5);
    }
}
