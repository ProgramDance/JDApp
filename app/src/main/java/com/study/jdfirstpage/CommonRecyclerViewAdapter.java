package com.study.jdfirstpage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 通用RecyclerView Adapter
 * Created by wangheng on 2018/3/10.
 * http://blog.csdn.net/z240336124/article/details/53909481
 */

public abstract class CommonRecyclerViewAdapter<T> extends RecyclerView.Adapter<CommonRecyclerViewAdapter.CommonViewHolder> {
    private static final String TAG = CommonRecyclerViewAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_HEAD = 1;
    private static final int VIEW_TYPE_CONTENT = 2;
    private static final int VIEW_TYPE_FOOT = 3;
    private Context mContext;
    private int mLayoutId;
    private int mHeadLayoutId;
    private int mFootLayoutId;
    private List<T> mDatas;
    private OnItemClickListener mOnItemClickListener;
    private boolean isShowFoot;

    public CommonRecyclerViewAdapter(Context context, int layoutId, List<T> datas) {
        mContext = context;
        mLayoutId = layoutId;
        mDatas = datas;
    }

    public CommonRecyclerViewAdapter(Context context, int layoutId, List<T> datas, boolean isShowFoot) {
        this(context, layoutId, datas);
        this.isShowFoot = isShowFoot;
    }

    /**
     * 子类实现bind数据
     *
     * @param holder   点击item对应的ViewHolder
     * @param position 点击item对应的position
     */
    public abstract void bind(CommonViewHolder holder, int position);

    public void setmHeadLayoutId(int mHeadLayoutId) {
        this.mHeadLayoutId = mHeadLayoutId;
    }

    public void setmFootLayoutId(int mFootLayoutId) {
        this.mFootLayoutId = mFootLayoutId;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowFoot) {
            if (position >= mDatas.size()) {
                return VIEW_TYPE_FOOT;
            } else {
                return VIEW_TYPE_CONTENT;
            }
        } else if(mHeadLayoutId != 0 && position == 0){
            return VIEW_TYPE_HEAD;
        }else {
            return VIEW_TYPE_CONTENT;
        }
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_HEAD) {
            view = LayoutInflater.from(mContext).inflate(mHeadLayoutId, parent, false);
            CommonViewHolder commonViewHolder = new CommonViewHolder(view);
            Log.i(TAG, "onCreateViewHolder--holder:" + commonViewHolder.toString());
            return commonViewHolder;
        } else if (viewType == VIEW_TYPE_FOOT) {
            view = LayoutInflater.from(mContext).inflate(mFootLayoutId, parent, false);
            CommonViewHolder commonViewHolder = new CommonViewHolder(view);
            Log.i(TAG, "onCreateViewHolder--holder:" + commonViewHolder.toString());
            return commonViewHolder;
        } else {
            view = LayoutInflater.from(mContext).inflate(mLayoutId, parent, false);
            CommonViewHolder commonViewHolder = new CommonViewHolder(view);
            Log.i(TAG, "onCreateViewHolder--holder:" + commonViewHolder.toString());
            return commonViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(final CommonRecyclerViewAdapter.CommonViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder--holder:" + holder.toString());

        if (isShowFoot && position >= mDatas.size()) {

        } else {
            bind(holder, position);

            // 设置点击监听
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onClick(v, holder.getLayoutPosition());
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mOnItemClickListener.onLongClick(v, holder.getLayoutPosition());
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return isShowFoot ? mDatas.size() + 1 : mDatas.size();
    }

    public class CommonViewHolder extends RecyclerView.ViewHolder {
        // 用来存放子View减少findViewById的次数
        private SparseArray<View> mViews;

        public CommonViewHolder(View itemView) {
            super(itemView);
            mViews = new SparseArray<>();
        }

        public View getView(int id) {
            View view = mViews.get(id);
            if (view == null) {
                Log.i(TAG, "getView2:" + id);
                view = itemView.findViewById(id);
                mViews.put(id, view);
            } else {
                Log.i(TAG, "getView1:" + id);
            }

            return view;
        }
    }

    /**
     * 点击监听
     */
    public interface OnItemClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void addData(int position, T data) {
        mDatas.add(position, data);
        notifyItemInserted(position);
    }

    public void addDatas(List<T> datas) {
        int position = mDatas.size();
        mDatas.addAll(datas);
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    public void updateData(int position, T data) {
        mDatas.set(position, data);
        notifyItemChanged(position);
    }

    public void setShowFoot(boolean showFoot) {
        isShowFoot = showFoot;
        notifyDataSetChanged();
    }
}
