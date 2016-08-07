package com.loopeer.directorychooser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DirectoryFileAdapter extends RecyclerDirectoryBaseAdapter<FileNod> {

    public interface OnDirectoryClickListener {
        void onDirectoryClick(FileNod node);
    }

    public interface OnNodeSelectListener {
        void onNodeSelected(FileNod node);
    }

    private OnDirectoryClickListener mItemClickListener;
    private OnNodeSelectListener mNodeSelectListener;

    public DirectoryFileAdapter(Context context) {
        super(context);
    }

    public void setItemClickListener(OnDirectoryClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setNodeSelectListener(OnNodeSelectListener nodeSelectListener) {
        mNodeSelectListener = nodeSelectListener;
    }

    @Override
    public void bindView(final FileNod var1, int var2, RecyclerView.ViewHolder var3) {
        DirectoryFileViewHolder viewHolder = (DirectoryFileViewHolder) var3;
        viewHolder.bind(var1);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onDirectoryClick(var1);
            }
        });
        viewHolder.mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNodeSelectListener.onNodeSelected(var1);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.list_item_directory_chooser, parent, false);
        return new DirectoryFileViewHolder(view);
    }

    class DirectoryFileViewHolder extends RecyclerView.ViewHolder {

        TextView mTextName;
        View mSelectBtn;

        public DirectoryFileViewHolder(View itemView) {
            super(itemView);
            mTextName = (TextView) itemView.findViewById(R.id.text_directory_name);
            mSelectBtn = itemView.findViewById(R.id.btn_directory_select);
        }

        public void bind(FileNod nod) {
            mTextName.setText(nod.name);
            int drawableId = nod.isFolder ? R.drawable.ic_directory_path : R.drawable.ic_directory_file;
            Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), drawableId);
            mTextName.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }

    }

}
