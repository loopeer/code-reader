package com.loopeer.codereader.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopeer.codereader.Navigator;
import com.loopeer.codereader.R;
import com.loopeer.codereader.model.Repository;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RepositoryAdapter extends RecyclerViewAdapter<Repository> {

    private boolean mHasMore;

    public RepositoryAdapter(Context context) {
        super(context);
    }

    public void setHasMore(boolean hasMore) {
        mHasMore = hasMore;
    }

    @Override
    public void bindView(Repository repository, int position, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof RepositoryViewHolder) {
            RepositoryViewHolder holder = (RepositoryViewHolder) viewHolder;
            holder.bind(repository, position);
        }
    }

    @Override
    public Repository getItem(int position) {
        if (isFooterPositon(position))
            return null;
        return super.getItem(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case R.layout.view_footer_loading: {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.view_footer_loading, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }
            default: {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_repository, parent, false);
                return new RepositoryViewHolder(view);
            }
        }
    }

    private boolean isFooterPositon(int position) {
        if (mHasMore && position == getItemCount() - 1)
            return true;
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooterPositon(position))
            return R.layout.view_footer_loading;
        return R.layout.list_item_repository;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + (mHasMore ? 1 : 0);
    }

    class RepositoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_avatar)
        ImageView mImgAvatar;
        @BindView(R.id.txt_full_name)
        TextView mTxtFullName;
        @BindView(R.id.txt_description)
        TextView mTxtDescription;

        RepositoryViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(Repository repository, int position) {
            Glide.with(getContext()).load(repository.owner.avatarUrl).into(mImgAvatar);
            mTxtFullName.setText(repository.fullName);
            mTxtDescription.setText(repository.description);

            itemView.setOnClickListener(view -> {
                Navigator.startWebActivity(getContext(), repository.htmlUrl);
            });
        }
    }
}
