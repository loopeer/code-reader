package com.loopeer.codereader.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopeer.codereader.Navigator;
import com.loopeer.codereader.R;
import com.loopeer.codereader.model.MainHeaderItem;
import com.loopeer.codereader.model.Repo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainLatestAdapter extends RecyclerViewAdapter<Repo> {

    public MainLatestAdapter(Context context) {
        super(context);
    }

    @Override
    public void setData(List<Repo> data) {
        data.add(0, null);
        super.setData(data);
    }

    @Override
    public void bindView(Repo var1, int var2, RecyclerView.ViewHolder var3) {
        if (var3 instanceof RepoViewHolder) {
            RepoViewHolder viewHolder = (RepoViewHolder) var3;
            viewHolder.bind(var1);
            viewHolder.itemView.setOnClickListener(view -> Navigator.startCodeReadActivity(getContext(), var1));
        }
        if (var3 instanceof MainHeaderHolder) {
            MainHeaderHolder viewHolder = (MainHeaderHolder) var3;
            viewHolder.bind();
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = getLayoutInflater();
        View view;
        switch (viewType) {
            case R.layout.list_item_main_top_header:
                view = inflater.inflate(R.layout.list_item_main_top_header, parent, false);
                return new MainHeaderHolder(view);
            default:
                view = inflater.inflate(R.layout.list_item_repo, parent, false);
                return new RepoViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return R.layout.list_item_main_top_header;
        return R.layout.list_item_repo;
    }

    class RepoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_repo_type)
        ImageView mImgRepoType;
        @BindView(R.id.text_repo_name)
        TextView mTextRepoName;
        @BindView(R.id.text_repo_time)
        TextView mTextRepoTime;

        public RepoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Repo repo) {
            mImgRepoType.setBackgroundResource(repo.isFolder ? R.drawable.shape_circle_folder : R.drawable.shape_circle_document);
            mImgRepoType.setImageResource(repo.isFolder ? R.drawable.ic_repo_white : R.drawable.ic_document_white);
            mTextRepoName.setText(repo.name);
            mTextRepoTime.setText(DateUtils.getRelativeTimeSpanString(itemView.getContext(), repo.lastModify));
        }
    }

    class MainHeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.grid_main)
        GridView mGridView;
        private MainHeaderAdapter mMainHeaderAdapter;
        public MainHeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mMainHeaderAdapter = new MainHeaderAdapter(itemView.getContext());
            mGridView.setAdapter(mMainHeaderAdapter);
        }

        public void bind() {
            List<MainHeaderItem> items = new ArrayList<>();
            items.add(new MainHeaderItem(R.drawable.ic_github, R.string.header_item_github_search, ""));
            items.add(new MainHeaderItem(R.drawable.ic_trending, R.string.header_item_trending, ""));
            mMainHeaderAdapter.updateData(items);
        }
    }
}
