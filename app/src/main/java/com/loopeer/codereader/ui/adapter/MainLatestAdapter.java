package com.loopeer.codereader.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopeer.codereader.R;
import com.loopeer.codereader.model.Repo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainLatestAdapter extends RecyclerViewAdapter<Repo> {

    public MainLatestAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindView(Repo var1, int var2, RecyclerView.ViewHolder var3) {
        RepoViewHolder viewHolder = (RepoViewHolder) var3;
        viewHolder.bind(var1);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.list_item_repo, parent, false);
        return new RepoViewHolder(view);
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
            mTextRepoName.setText("sjgl");
            mTextRepoTime.setText(String.valueOf("12123123123"));
            /*mTextRepoName.setText(repo.name);
            mTextRepoTime.setText(String.valueOf(repo.latestOpenTime));*/
        }
    }
}
