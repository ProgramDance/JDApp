package com.study.jdfirstpage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.ColumnLayoutHelper;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.android.vlayout.layout.OnePlusNLayoutHelper;
import com.bumptech.glide.Glide;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.google.gson.Gson;
import com.study.jdfirstpage.bean.HomeBean;
import com.study.jdfirstpage.bean.HomeBean.ItemInfoListBean.ItemContentListBean;
import com.study.jdfirstpage.bean.RecommendBean;
import com.study.jdfirstpage.utils.StatusBarUtils;
import com.study.jdfirstpage.view.PullRefreshLayout;
import com.sunfusheng.marqueeview.MarqueeView;
import com.wh.uilib.banner.Banner;
import com.wh.uilib.slidemenu.TextImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;
import in.srain.cube.views.ptr.util.PtrCLog;

public class MainActivity extends Activity {
    class ViewType {
        public static final int BANNER = 1;
        public static final int MENU = 2;
        public static final int ITEM_IMG = 3;
        public static final int BULLETIN = 4;
        public static final int ITEM_IMG_TEXT = 5;
    }

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.ll_head)
    View head;
    @BindView(R.id.status_mask)
    View statusMask;
    @BindView(R.id.pull_refresh)
    PullRefreshLayout pullRefreshLayout;

    private float scrollDistance;
    private int headHeight;
    private Integer[] imgsInt = {R.drawable.banner1, R.drawable.banner2, R.drawable.banner3,
            R.drawable.banner4, R.drawable.banner5};
    private HomeBean homeBean;
    private RecommendBean recommendBean;
    private int marginLeftRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        StatusBarUtils.setFullScreenWithStatusBar(this);

        head.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                headHeight = head.getHeight();
                head.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        initData();

        initView();
    }

    private void initData() {
        try {
            InputStream is = getAssets().open("homeindex.txt");
            Reader reader = new InputStreamReader(is);
            Gson gson = new Gson();
            homeBean = gson.fromJson(reader, HomeBean.class);

            is = getAssets().open("recommend.txt");
            reader = new InputStreamReader(is);
            recommendBean = gson.fromJson(reader, RecommendBean.class);

            marginLeftRight = getResources().getDimensionPixelSize(R.dimen.main_mragin_left_right);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        initVLayout();

        initScrollListener();

        ViewGroup.LayoutParams layoutParams = statusMask.getLayoutParams();
        layoutParams.height = StatusBarUtils.getStatusBarHeight(this);

        initPullRefresh();
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                float oldScrollDistance = scrollDistance;
                scrollDistance += dy;
                Log.e("tag", "onScrolled>>dy:" + dy + ",scrollDistance:" + scrollDistance);
                if (headHeight != 0) {
                    float alpha = ((scrollDistance / headHeight));
                    alpha = alpha > 1.0f ? 1.0f : alpha;
                    if (alpha >= 0.5f) {
                        StatusBarUtils.setStatusBarLightMode(MainActivity.this);
                    } else {
                        StatusBarUtils.setStatusBarDarkMode(MainActivity.this);
                    }
                    int color = Color.argb((int) (alpha * 0xff), 0xff, 0xff, 0xff);
                    head.setBackgroundColor(color);

                    if (alpha > 0.2f && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        statusMask.setBackgroundColor(Color.DKGRAY);
                    } else {
                        statusMask.setBackground(null);
                    }
                }
            }
        });
    }

    private int[] menuImgId = {R.drawable.icon_menu_market, R.drawable.icon_menu_clothes, R.drawable.icon_menu_fresh,
            R.drawable.icon_menu_go_home, R.drawable.icon_menu_recharge, R.drawable.icon_menu_world_buy,
            R.drawable.icon_menu_recharge, R.drawable.icon_menu_world_buy};
    private String[] menuText = {"京东超市", "京东服饰", "京东生鲜", "京东到家", "缴费", "全球购", "缴费", "全球购"};

    private void initVLayout() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(this);
        recyclerView.setLayoutManager(virtualLayoutManager);
        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager, true);
        recyclerView.setAdapter(delegateAdapter);

        List<DelegateAdapter.Adapter> adapters = new ArrayList<>();
        adapters.add(new MyAdapter(this, new LinearLayoutHelper(), 1, ViewType.BANNER) {

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_banner, parent, false));
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                Banner banner = (Banner) holder.itemView;
                if (banner != null) {
                    banner.setContent(Arrays.asList(imgsInt));
                }
            }
        });

        GridLayoutHelper layoutHelper;
        layoutHelper = new GridLayoutHelper(4);
        layoutHelper.setMargin(0, 5, 0, 5); // 设置整个grideview上边和下边的间距
        layoutHelper.setAspectRatio(5f);
        adapters.add(new MyAdapter(this, layoutHelper, 8, ViewType.MENU) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_menu, parent, false));
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                TextImageView textImageView = (TextImageView) holder.itemView;
                textImageView.setImgId(menuImgId[position]);
                textImageView.setText(menuText[position]);
            }
        });

        final List<ItemContentListBean> itemContentList = getItemContentList("newUser");
        OnePlusNLayoutHelper onePlusNLayoutHelper = new OnePlusNLayoutHelper();
        onePlusNLayoutHelper.setMarginLeft(marginLeftRight);
        onePlusNLayoutHelper.setMarginRight(marginLeftRight);
        adapters.add(new MyAdapter(this, onePlusNLayoutHelper, 3, ViewType.ITEM_IMG + 33) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_image, parent, false));
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                ImageView imageView = (ImageView) holder.itemView;
                Log.e("tag", "OnePlusN w:" + imageView.getMeasuredWidth() + ",h:" + imageView.getMeasuredHeight());
                Uri uri = Uri.parse(itemContentList.get(position + 1).getImageUrl());
                Glide.with(MainActivity.this).load(uri).into(imageView);
            }
        });

        LinearLayoutHelper newUserLlLayoutHelper = new LinearLayoutHelper();
        newUserLlLayoutHelper.setMargin(marginLeftRight, 0, marginLeftRight, 0);
        adapters.add(new MyAdapter(this, newUserLlLayoutHelper, 1, ViewType.ITEM_IMG + 34) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_image, parent, false));
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                ImageView imageView = (ImageView) holder.itemView;
                Uri uri = Uri.parse(itemContentList.get(4).getImageUrl());
                Glide.with(MainActivity.this).load(uri).into(imageView);
            }
        });

        List<ItemContentListBean> itemBulletinContentList = getItemContentList("jdBulletin");
        final List<String> bulletinList = new ArrayList<>();
        for (ItemContentListBean itemContent : itemBulletinContentList) {
            bulletinList.add(itemContent.getItemSubTitle());
        }
        adapters.add(new MyAdapter(this, newUserLlLayoutHelper, 1, ViewType.BULLETIN) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_bulletin, parent, false));
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                MarqueeView marqueeView = (MarqueeView) holder.itemView.findViewById(R.id.marquee_view);
                marqueeView.startWithList(bulletinList);
            }
        });

        final List<ItemContentListBean> showEventList = getItemContentList("showEvent");
        ColumnLayoutHelper columnLayoutHelper = new ColumnLayoutHelper();
        columnLayoutHelper.setMargin(marginLeftRight, 0, marginLeftRight, 0);
        adapters.add(new MyAdapter(this, columnLayoutHelper, 3, ViewType.ITEM_IMG) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_image, parent, false));
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                ImageView imageView = (ImageView) holder.itemView;
                imageView.requestLayout();
                Log.e("tag", "3img w:" + imageView.getMeasuredWidth() + ",h:" + imageView.getMeasuredHeight());
                Uri uri = Uri.parse(showEventList.get(position).getImageUrl());
                Glide.with(MainActivity.this).load(uri).into(imageView);
            }
        });

        final List<ItemContentListBean> findGoodList = getItemContentList("findGoodStuff");
        GridLayoutHelper findGoodLayoutHelper = new GridLayoutHelper(2);
        findGoodLayoutHelper.setWeights(new float[]{50f, 50f});
        findGoodLayoutHelper.setHGap(3);
        findGoodLayoutHelper.setMargin(marginLeftRight, 0, marginLeftRight, 3);
        adapters.add(new MyAdapter(this, findGoodLayoutHelper, 2, ViewType.ITEM_IMG_TEXT) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_image_text, parent, false));
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                LinearLayout linearLayout = (LinearLayout) holder.itemView;
                ImageView imageView = (ImageView) linearLayout.findViewById(R.id.iv_img_one);
                Uri uri = Uri.parse(findGoodList.get(position).getImageUrl());
                Glide.with(MainActivity.this).load(uri).into(imageView);
            }
        });

        final List<ItemContentListBean> rankingList = getItemContentList("ranking");
        GridLayoutHelper rangkingLayoutHelper = new GridLayoutHelper(3);
        rangkingLayoutHelper.setWeights(new float[]{50f, 25f, 25f});
        rangkingLayoutHelper.setHGap(3);
        rangkingLayoutHelper.setMargin(marginLeftRight, 0, marginLeftRight, 3);
        adapters.add(new MyAdapter(this, rangkingLayoutHelper, 3, ViewType.ITEM_IMG_TEXT) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_image_text, parent, false));
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                LinearLayout linearLayout = (LinearLayout) holder.itemView;
                linearLayout.requestLayout();

                ImageView imageView = linearLayout.findViewById(R.id.iv_img_one);
                Uri uri = Uri.parse(rankingList.get(position).getImageUrl());
                Glide.with(MainActivity.this).load(uri).into(imageView);
            }
        });

        // 爱生活
        final List<ItemContentListBean> loveLifeList = getItemContentList("loveLife");
        LinearLayoutHelper loveLifeLayoutHelper = new LinearLayoutHelper();
        loveLifeLayoutHelper.setMargin(marginLeftRight, 0, marginLeftRight, 0);
        adapters.add(new MyAdapter(this, loveLifeLayoutHelper, 1, 999) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_image_wrapper, parent, false));
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                ImageView imageView = (ImageView) holder.itemView.findViewById(R.id.image_view);
                Uri uri = Uri.parse(loveLifeList.get(position).getImageUrl());
                Glide.with(MainActivity.this).load(uri).into(imageView);
            }
        });

        final List<ItemContentListBean> loveLifeType22List = getItemContentList("type_22", "loveLife");
        GridLayoutHelper loveLifeType22LayoutHelper = new GridLayoutHelper(2);
        loveLifeType22LayoutHelper.setMargin(marginLeftRight, 0, marginLeftRight, 3);
        loveLifeType22LayoutHelper.setHGap(3);
        loveLifeType22LayoutHelper.setVGap(3);
        adapters.add(new MyAdapter(this, loveLifeType22LayoutHelper, 4, ViewType.ITEM_IMG_TEXT) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_image_text, parent, false));
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                LinearLayout linearLayout = (LinearLayout) holder.itemView;

                ItemContentListBean content = loveLifeType22List.get(position);

                ImageView imageView = linearLayout.findViewById(R.id.iv_img_one);
                Uri uri = Uri.parse(content.getImageUrl());
                Glide.with(MainActivity.this).load(uri).into(imageView);

                TextView tvTitle = linearLayout.findViewById(R.id.tv_title);
                TextView tvSubTitle = linearLayout.findViewById(R.id.tv_sub_title);
                TextView tvRecommendedTitle = linearLayout.findViewById(R.id.tv_recommended_title);
                tvTitle.setText(content.getItemTitle());
                tvSubTitle.setText(content.getItemSubTitle());
                tvRecommendedTitle.setText(content.getItemRecommendedLanguage());
            }
        });

        final List<ItemContentListBean> loveLifeType1111List = getItemContentList("type_1111", "loveLife");
        GridLayoutHelper loveLifeType1111LayoutHelper = new GridLayoutHelper(4);
        loveLifeType1111LayoutHelper.setMargin(marginLeftRight, 0, marginLeftRight, 0);
        loveLifeType1111LayoutHelper.setHGap(3);
        loveLifeType1111LayoutHelper.setVGap(3);
        adapters.add(new MyAdapter(this, loveLifeType1111LayoutHelper, 4, ViewType.ITEM_IMG_TEXT) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_image_text, parent, false));
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                LinearLayout linearLayout = (LinearLayout) holder.itemView;
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                ItemContentListBean content = loveLifeType1111List.get(position);

                ImageView imageView = linearLayout.findViewById(R.id.iv_img_one);
                Uri uri = Uri.parse(content.getImageUrl());
                Glide.with(MainActivity.this).load(uri).into(imageView);

                TextView tvTitle = linearLayout.findViewById(R.id.tv_title);
                TextView tvSubTitle = linearLayout.findViewById(R.id.tv_sub_title);
                TextView tvRecommendedTitle = linearLayout.findViewById(R.id.tv_recommended_title);
                tvTitle.setText(content.getItemTitle());
                tvSubTitle.setText(content.getItemSubTitle());
                tvRecommendedTitle.setText(content.getItemRecommendedLanguage());
            }
        });

        // 享品质
        final List<ItemContentListBean> enjoyQualityList = getItemContentList("enjoyQuality");
        LinearLayoutHelper enjoyQualityLayoutHelper = new LinearLayoutHelper();
        enjoyQualityLayoutHelper.setMargin(marginLeftRight, 0, marginLeftRight, 0);
        adapters.add(new MyAdapter(this, enjoyQualityLayoutHelper, 1, 999) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_image_wrapper, parent, false));
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                ImageView imageView = (ImageView) holder.itemView.findViewById(R.id.image_view);
                Uri uri = Uri.parse(enjoyQualityList.get(position).getImageUrl());
                Glide.with(MainActivity.this).load(uri).into(imageView);
            }
        });

        final List<ItemContentListBean> enjoyQualityType22List = getItemContentList("type_22", "enjoyQuality");
        GridLayoutHelper enjoyQualityType22LayoutHelper = new GridLayoutHelper(2);
        enjoyQualityType22LayoutHelper.setMargin(marginLeftRight, 0, marginLeftRight, 3);
        enjoyQualityType22LayoutHelper.setHGap(3);
        enjoyQualityType22LayoutHelper.setVGap(3);
        adapters.add(new MyAdapter(this, enjoyQualityType22LayoutHelper, 4, ViewType.ITEM_IMG_TEXT) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_image_text, parent, false));
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                LinearLayout linearLayout = (LinearLayout) holder.itemView;

                ItemContentListBean content = enjoyQualityType22List.get(position);

                ImageView imageView = linearLayout.findViewById(R.id.iv_img_one);
                Uri uri = Uri.parse(content.getImageUrl());
                Glide.with(MainActivity.this).load(uri).into(imageView);

                TextView tvTitle = linearLayout.findViewById(R.id.tv_title);
                TextView tvSubTitle = linearLayout.findViewById(R.id.tv_sub_title);
                TextView tvRecommendedTitle = linearLayout.findViewById(R.id.tv_recommended_title);
                tvTitle.setText(content.getItemTitle());
                tvSubTitle.setText(content.getItemSubTitle());
                tvRecommendedTitle.setText(content.getItemRecommendedLanguage());
            }
        });

        final List<ItemContentListBean> enjoyQualityType1111List = getItemContentList("type_1111", "enjoyQuality");
        GridLayoutHelper enjoyQualityType1111LayoutHelper = new GridLayoutHelper(4);
        enjoyQualityType1111LayoutHelper.setMargin(marginLeftRight, 0, marginLeftRight, 0);
        enjoyQualityType1111LayoutHelper.setHGap(3);
        enjoyQualityType1111LayoutHelper.setVGap(3);
        adapters.add(new MyAdapter(this, enjoyQualityType1111LayoutHelper, 4, ViewType.ITEM_IMG_TEXT) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_image_text, parent, false));
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                LinearLayout linearLayout = (LinearLayout) holder.itemView;
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                ItemContentListBean content = enjoyQualityType1111List.get(position);

                ImageView imageView = linearLayout.findViewById(R.id.iv_img_one);
                Uri uri = Uri.parse(content.getImageUrl());
                Glide.with(MainActivity.this).load(uri).into(imageView);

                TextView tvTitle = linearLayout.findViewById(R.id.tv_title);
                TextView tvSubTitle = linearLayout.findViewById(R.id.tv_sub_title);
                TextView tvRecommendedTitle = linearLayout.findViewById(R.id.tv_recommended_title);
                tvTitle.setText(content.getItemTitle());
                tvSubTitle.setText(content.getItemSubTitle());
                tvRecommendedTitle.setText(content.getItemRecommendedLanguage());
            }
        });

        // 推荐
        final List<RecommendBean.ItemInfoListBean> recommendList = recommendBean.getItemInfoList();
        GridLayoutHelper recommendLayoutHelper = new GridLayoutHelper(2);
        recommendLayoutHelper.setMargin(marginLeftRight, 5, marginLeftRight, 5); // 设置整个grideview上边和下边的间距
        recommendLayoutHelper.setHGap(6); // 设置item间横向间隔
        recommendLayoutHelper.setVGap(6); // 设置item间纵向间隔
        adapters.add(new MyAdapter(this, recommendLayoutHelper, recommendList.size(), 23) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_recommend, parent, false));
            }

            @Override
            public void onBindViewHolder(MyViewHolder holder, int position) {
                LinearLayout linearLayout = (LinearLayout) holder.itemView;
                ImageView imageView = linearLayout.findViewById(R.id.recommended_img);
                TextView tvTitle = linearLayout.findViewById(R.id.recommended_title);
                TextView tvPrice = linearLayout.findViewById(R.id.recommended_price);
                RecommendBean.ItemInfoListBean item = recommendList.get(position);

                Uri uri = Uri.parse(item.getItemContentList().get(0).getImageUrl());
                Glide.with(MainActivity.this).load(uri).into(imageView);
                tvTitle.setText(item.getItemContentList().get(0).getItemTitle());
                tvPrice.setText(item.getItemContentList().get(0).getItemSubTitle());
            }
        });

        delegateAdapter.addAdapters(adapters);
    }

    class MyAdapter extends DelegateAdapter.Adapter<MyViewHolder> {
        private Context mContext;
        private LayoutHelper mLayoutHelper;
        private VirtualLayoutManager.LayoutParams mLayoutParams;
        private int mCount = 0;
        private int mViewType;

        public MyAdapter(Context context, LayoutHelper layoutHelper, int count) {
            this(context, layoutHelper, count, new VirtualLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
        }

        public MyAdapter(Context context, LayoutHelper layoutHelper, int count, int viewType) {
            this(context, layoutHelper, count, new VirtualLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            this.mViewType = viewType;
        }

        public MyAdapter(Context context, LayoutHelper layoutHelper, int count, @NonNull VirtualLayoutManager.LayoutParams layoutParams) {
            this.mContext = context;
            this.mLayoutHelper = layoutHelper;
            this.mCount = count;
            this.mLayoutParams = layoutParams;
        }

        @Override
        public LayoutHelper onCreateLayoutHelper() {
            return mLayoutHelper;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.itemView.setLayoutParams(
                    new VirtualLayoutManager.LayoutParams(mLayoutParams));
        }

        @Override
        public int getItemCount() {
            return mCount;
        }

        @Override
        public int getItemViewType(int position) {
            return mViewType;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 获取指定type的数据
     *
     * @param itemType
     * @return
     */
    private List<ItemContentListBean> getItemContentList(String itemType) {
        List<HomeBean.ItemInfoListBean> itemInfoList = homeBean.getItemInfoList();
        int count = itemInfoList.size();
        for (int i = 0; i < count; i++) {
            if (itemType.equals(itemInfoList.get(i).getModule())) {
                return itemInfoList.get(i).getItemContentList();
            }
        }

        return null;
    }

    private List<ItemContentListBean> getItemContentList(String itemType, String module) {
        List<HomeBean.ItemInfoListBean> itemInfoList = homeBean.getItemInfoList();
        int count = itemInfoList.size();
        for (int i = 0; i < count; i++) {
            if (itemType.equals(itemInfoList.get(i).getItemType()) && module.equals(itemInfoList.get(i).getModule())) {
                return itemInfoList.get(i).getItemContentList();
            }
        }

        return null;
    }

    private void initPullRefresh() {
        PtrFrameLayout.DEBUG = true;
        PtrCLog.setLogLevel(PtrCLog.LEVEL_VERBOSE);

        pullRefreshLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                Toast.makeText(MainActivity.this, "开始下拉刷新", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullRefreshLayout.refreshComplete();
                    }
                }, 2000);
            }
        });

//        pullRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void loadMore() {
//                Toast.makeText(MainActivity.this, "加载更多", Toast.LENGTH_SHORT).show();
//            }
//        });
//        pullRefreshLayout.setLoadMoreEnable(true);

        pullRefreshLayout.addPtrUIHandler(new PtrUIHandler() {
            @Override
            public void onUIReset(PtrFrameLayout frame) {
                head.setVisibility(View.VISIBLE);
                pullRefreshLayout.setEnabled(true);
            }

            @Override
            public void onUIRefreshPrepare(PtrFrameLayout frame) {
                head.setVisibility(View.GONE);
            }

            @Override
            public void onUIRefreshBegin(PtrFrameLayout frame) {
                pullRefreshLayout.setEnabled(false);
            }

            @Override
            public void onUIRefreshComplete(PtrFrameLayout frame) {
            }

            @Override
            public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

            }
        });
    }
}
