package com.loopeer.directorychooser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerDirectoryBaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<T> mData;

    public RecyclerDirectoryBaseAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = new ArrayList<>();
    }

    public void updateData(List<T> data) {
        this.setData(data);
        this.notifyDataSetChanged();
    }

    public void setData(List<T> data) {
        this.mData.clear();
        if (data != null) {
            this.mData.addAll(data);
        }

    }

    public LayoutInflater getLayoutInflater() {
        return this.mInflater;
    }

    public Context getContext() {
        return this.mContext;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        T data = this.getItem(position);
        this.bindView(data, position, holder);
    }

    public abstract void bindView(T var1, int var2, RecyclerView.ViewHolder var3);

    public T getItem(int position) {
        return this.mData.get(position);
    }

    public int getItemCount() {
        return this.mData == null ? 0 : this.mData.size();
    }
}