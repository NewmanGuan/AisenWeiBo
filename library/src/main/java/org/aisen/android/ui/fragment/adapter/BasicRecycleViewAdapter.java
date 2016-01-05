package org.aisen.android.ui.fragment.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.aisen.android.ui.fragment.APagingFragment;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 1、支持RecycleView
 * 2、支持ViewType，默认是Normal Type
 *
 * Created by wangdan on 16/1/5.
 */
public class BasicRecycleViewAdapter<T extends Serializable> extends RecyclerView.Adapter implements IPagingAdapter {

    private APagingFragment holderFragment;
    private ArrayList<T> datas;
    private IITemView<T> footerItemView;

    public BasicRecycleViewAdapter(APagingFragment holderFragment, ArrayList<T> datas) {
        if (datas == null)
            datas = new ArrayList<T>();
        this.holderFragment = holderFragment;
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void addFooterView(IITemView<T> footerItemView) {
        this.footerItemView = footerItemView;
    }

    @Override
    public int getItemViewType(int position) {
        if (footerItemView != null && position == getItemCount() - 1) {
            return IPagingAdapter.TYPE_FOOTER;
        }

        return IPagingAdapter.TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int[][] itemResAndTypeArr = holderFragment.configItemViewAndType();

        int itemRes = -1;
        int itemType = viewType;

        if (itemType != IPagingAdapter.TYPE_FOOTER) {
            for (int[] itemResAndType : itemResAndTypeArr) {
                if (itemType == itemResAndType[1]) {
                    itemRes = itemResAndType[0];

                    break;
                }
            }

            if (itemRes == -1) {
                throw new RuntimeException("没有找到ViewRes，ViewType = " + itemType);
            }
        }

        View convertView;
        IITemView<T> itemView;
        if (viewType == IPagingAdapter.TYPE_FOOTER) {
            itemView = footerItemView;

            convertView = itemView.getConvertView();
        }
        else {
            convertView = View.inflate(holderFragment.getActivity(), itemRes, null);

            itemView = holderFragment.newItemView(convertView, viewType);
        }
        itemView.bindingView(convertView);

        if (!(itemView instanceof ARecycleViewItemView)) {
            throw new RuntimeException("RecycleView只支持ARecycleViewItemView，请重新配置");
        }

        return (ARecycleViewItemView) itemView;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ARecycleViewItemView itemView = (ARecycleViewItemView) holder;

        itemView.reset(datas.size(), position);
        if (position < datas.size()) {
            itemView.onBindData(itemView.getConvertView(), datas.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size() + (footerItemView == null ? 0 : 1);
    }

    @Override
    public ArrayList getDatas() {
        return datas;
    }

}
