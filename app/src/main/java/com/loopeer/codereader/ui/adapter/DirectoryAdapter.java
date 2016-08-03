package com.loopeer.codereader.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopeer.codereader.R;
import com.loopeer.codereader.model.DirectoryNode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DirectoryAdapter extends RecyclerViewAdapter<DirectoryNode> {

    public DirectoryAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindView(DirectoryNode var1, int var2, RecyclerView.ViewHolder var3) {
        DirectoryViewHolder viewHolder = (DirectoryViewHolder) var3;
        viewHolder.bind(var1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.list_item_directory, parent, false);
        return new DirectoryViewHolder(view);
    }

    class DirectoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_directory_name)
        TextView mTextDirectoryName;
        @BindView(R.id.img_directory_open_close)
        ImageView mImgOpenClose;

        public DirectoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(DirectoryNode pathNode) {
            mTextDirectoryName.setText(pathNode.name);
        }
    }
}
