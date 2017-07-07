package com.uestc.magicwo.livecampus.index;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uestc.magicwo.livecampus.R;
import com.uestc.magicwo.livecampus.models.RoomBaseInfoResponse;
import com.uestc.magicwo.livecampus.net.Urls;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.magicwo.com.magiclib.constants.RefreshConstant.LOADING_MORE;
import static com.magicwo.com.magiclib.constants.RefreshConstant.NO_LOAD_MORE;
import static com.magicwo.com.magiclib.constants.RefreshConstant.PULLUP_LOAD_MORE;

/**
 * Created by Magicwo on 2017/1/8.
 * 主页view的适配器
 */

public class IndexAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;//正常的item
    private static final int TYPE_FOOTER = 1;//尾部
    private static final int TYPE_HEADER = 2;//头部


    private List<RoomBaseInfoResponse> datas;
    private Context context;
    private int mLoadMoreStatus = PULLUP_LOAD_MORE;

    private View mHeaderView;
    private View mFooterView;

    private OnItemClickListener mListener;
    private boolean ifEmptyView = false;


    public IndexAdapter(Context context, List<RoomBaseInfoResponse> datas) {
        this.context = context;
        this.datas = datas;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
    }

    public void setIfEmptyView(boolean ifEmptyView) {
        this.ifEmptyView = ifEmptyView;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.index_item_view_recycler, parent, false);
            ItemViewHolder holder = new ItemViewHolder(view);
            return holder;
        } else if (viewType == TYPE_FOOTER) {
            mFooterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.foot_recycler, parent, false);
            return new FooterViewHolder(mFooterView);
        } else if (viewType == TYPE_HEADER && mHeaderView != null) {
            return new ItemViewHolder(mHeaderView);
        }

        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        if (holder instanceof ItemViewHolder) {
            final int pos = getRealPosition(holder);
            final RoomBaseInfoResponse data = datas.get(pos);
            if (data != null) {
                ((ItemViewHolder) holder).contentTextView.setText(data.getDescription());
//                ((ItemViewHolder) holder).audienceTextView.setText(String.valueOf(data.getAudience_num()));
                if (data.getCover() == null || data.getCover().equals("")) {
                    Glide.with(context).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498155090964&di=f332a920017af3b784245f5d2bd6c9d5&imgtype=0&src=http%3A%2F%2Fimgstore.cdn.sogou.com%2Fapp%2Fa%2F100540002%2F481521.jpg").into(((ItemViewHolder) holder).imageView);
                } else {
                    Glide.with(context).load(Urls.SERVER+data.getCover()).into(((ItemViewHolder) holder).imageView);
                }
                ((ItemViewHolder) holder).nickNameTextView.setText(data.getUsername());
            }
            if (mListener == null) return;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(pos, data.getRid());
                }
            });


        } else if (holder instanceof FooterViewHolder) {

            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            if ((datas.size() <= 0) && ifEmptyView) {
                footerViewHolder.emptyLayout.setVisibility(View.VISIBLE);
            } else {
                footerViewHolder.emptyLayout.setVisibility(View.GONE);
            }

            switch (mLoadMoreStatus) {
                case PULLUP_LOAD_MORE:
//                    footerViewHolder.mTvLoadText.setText("上拉加载更多...");
//                    footerViewHolder.mLoadLayout.setVisibility(View.VISIBLE);
                    footerViewHolder.mLoadLayout.setVisibility(View.GONE);
                    break;
                case LOADING_MORE:
//                    footerViewHolder.mTvLoadText.setText("正加载更多...");
//                    footerViewHolder.mLoadLayout.setVisibility(View.VISIBLE);
                    footerViewHolder.mLoadLayout.setVisibility(View.GONE);
                    break;
                case NO_LOAD_MORE:
                    //隐藏加载更多
                    footerViewHolder.mLoadLayout.setVisibility(View.GONE);
                    break;

            }
        }

    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount())
            return TYPE_FOOTER;
        else if (position == 0 && mHeaderView != null) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    public void removeItem(int position) {
        datas.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        //RecyclerView的count设置为数据总条数+ 1（footerView）
        return mHeaderView == null ? datas.size() + 1 : datas.size() + 2;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.image_view)
        ImageView imageView;
        @BindView(R.id.content_text_view)
        TextView contentTextView;
        @BindView(R.id.nick_name_text_view)
        TextView nickNameTextView;
        @BindView(R.id.audience_text_view)
        TextView audienceTextView;
        @BindView(R.id.root_layout)
        LinearLayout rootLayout;

        ItemViewHolder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView)
                return;
            ButterKnife.bind(this, itemView);

        }


    }

    /**
     * 加载更多的ViewHolder
     */
    public class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pbLoad)
        ProgressBar mPbLoad;
        @BindView(R.id.tvLoadText)
        TextView mTvLoadText;
        @BindView(R.id.loadLayout)
        LinearLayout mLoadLayout;
        @BindView(R.id.empty_layout)
        LinearLayout emptyLayout;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    public void AddHeaderItem(List<RoomBaseInfoResponse> items) {
        datas.addAll(0, items);
        notifyDataSetChanged();
    }

    public void AddFooterItem(List<RoomBaseInfoResponse> items) {
        datas.addAll(items);
        notifyDataSetChanged();
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public void removeFooterView() {

    }

    public View getmHeaderView() {
        return mHeaderView;
    }

    /**
     * 更新加载更多状态
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        mLoadMoreStatus = status;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position, String data);
    }
}
