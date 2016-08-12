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

    public RepositoryAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindView(Repository repository, int position, RecyclerView.ViewHolder viewHolder) {
        RepositoryViewHolder holder = (RepositoryViewHolder) viewHolder;
        holder.bind(repository, position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_repository, parent, false);
        return new RepositoryViewHolder(view);
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
