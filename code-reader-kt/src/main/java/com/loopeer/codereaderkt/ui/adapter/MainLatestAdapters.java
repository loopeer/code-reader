package com.loopeer.codereaderkt.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.loopeer.codereaderkt.R;
import com.loopeer.codereaderkt.model.MainHeaderItem;
import com.loopeer.codereaderkt.model.Repo;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;


public class MainLatestAdapters extends RecyclerViewAdapter<Repo> {
    private static final String TAG = "MainLatestAdapters";

    public MainLatestAdapters(Context context) {
        super(context);
        Log.d(TAG + "log", "con");
    }

    private final CompositeSubscription mAllSubscription = new CompositeSubscription();

    @Override
    public void setData(List<Repo> data) {
        ArrayList list = new ArrayList();
        list.add(null);
        list.addAll(data);
        super.setData(list);
    }

    @Override
    public void bindView(Repo var1, int var2, RecyclerView.ViewHolder var3) {
        MainHeaderHolder viewHolder = (MainHeaderHolder) var3;
        Log.d(TAG + "log", "mainlatestbindview" );
        viewHolder.bind();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = getLayoutInflater();
        View view;
        view = inflater.inflate(R.layout.list_item_main_top_header, parent, false);
        return new MainHeaderHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
//        if (position == 0)
        return R.layout.list_item_main_top_header;
//        return R.layout.list_item_repo;
    }


    class MainHeaderHolder extends RecyclerView.ViewHolder {


        GridView mGridView;
        private MainHeaderAdapters mMainHeaderAdapter;

        public MainHeaderHolder(View itemView) {
            super(itemView);
            Log.d(TAG + "log", "headercon" );
            mGridView = itemView.findViewById(R.id.grid_main);
            mMainHeaderAdapter = new MainHeaderAdapters(itemView.getContext());
            mGridView.setAdapter(mMainHeaderAdapter);
        }

        public void bind() {
            Log.d(TAG + "log", "bindstart" );
            List<MainHeaderItem> items = new ArrayList<>();
            items.add(new MainHeaderItem(R.drawable.ic_github, R.string.header_item_github_search
                    , itemView.getContext().getString(R.string.header_item_github_search_link)));
            items.add(new MainHeaderItem(R.drawable.ic_trending, R.string.header_item_trending
                    , itemView.getContext().getString(R.string.header_item_trending_link)));
            Log.d(TAG + "log", "" + items.get(0).getName());
            mMainHeaderAdapter.updateData(items);
        }
    }
}
