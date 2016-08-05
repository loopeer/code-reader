package com.loopeer.codereader.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopeer.codereader.R;
import com.loopeer.codereader.model.DirectoryNode;
import com.loopeer.codereader.ui.view.DirectoryNavDelegate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DirectoryAdapter extends RecyclerViewAdapter<DirectoryNode> {

    DirectoryNode mNodeRoot;
    private DirectoryNavDelegate.FileClickListener mFileClickListener;

    public DirectoryAdapter(Context context, DirectoryNavDelegate.FileClickListener fileClickListener) {
        super(context);
        mFileClickListener = fileClickListener;
    }

    public void setNodeRoot(DirectoryNode root) {
        mNodeRoot = root;
        updateData(adaptNodes());
    }

    private List<DirectoryNode> adaptNodes() {
        ArrayList<DirectoryNode> nodes = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        createShowNodes(nodes, -1, mNodeRoot, sb);
        return nodes;
    }

    private void createShowNodes(ArrayList<DirectoryNode> nodes, int i, DirectoryNode nodeRoot, StringBuilder sb) {
        ++i;
        if (!nodeRoot.pathNodes.isEmpty()) {
            if (nodes.size() != 0 && nodeRoot.pathNodes.size() == 1 && nodeRoot.pathNodes.get(0).isDirectory) {
                nodeRoot.openChild = true;
                nodes.remove(nodes.size() - 1);
                --i;
                DirectoryNode node = nodeRoot.pathNodes.get(0);
                node.depth = i;
                sb.append(".");
                sb.append(node.name);
                node.displayName = sb.toString();
                nodes.add(node);
                if (node.openChild ||
                        (node.pathNodes != null
                                && node.pathNodes.size() == 1
                                && node.pathNodes.get(0).isDirectory)) {
                    createShowNodes(nodes, i, node, sb);
                }
            } else {
                for (DirectoryNode node : nodeRoot.pathNodes) {
                    if (sb.length() > 0) sb.delete(0, sb.length());
                    node.depth = i;
                    sb.append(node.name);
                    node.displayName = sb.toString();
                    nodes.add(node);
                    if (node.openChild ||
                            (node.pathNodes != null
                                    && node.pathNodes.size() == 1
                                    && node.pathNodes.get(0).isDirectory)) {
                        createShowNodes(nodes, i, node, sb);
                    }
                }
            }
        }
    }

    @Override
    public void bindView(final DirectoryNode var1, int var2, RecyclerView.ViewHolder var3) {
        DirectoryViewHolder viewHolder = (DirectoryViewHolder) var3;
        viewHolder.bind(var1);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (var1.isDirectory) {
                    var1.openChild = !var1.openChild;
                    updateData(adaptNodes());
                } else {
                    mFileClickListener.doOpenFile(var1);
                }
            }
        };
        viewHolder.itemView.setOnClickListener(clickListener);
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

        DirectoryNode mNode;

        public DirectoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(DirectoryNode pathNode) {
            mNode = pathNode;
            mTextDirectoryName.setText(pathNode.displayName);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            params.leftMargin = 20 * pathNode.depth;
            mImgOpenClose.setSelected(mNode.openChild);
            int drawableId = R.drawable.ic_directory_file;
            if (pathNode.isDirectory) {
                drawableId = R.drawable.ic_directory_path;
                mImgOpenClose.setVisibility(pathNode.pathNodes.isEmpty() ? View.INVISIBLE : View.VISIBLE);
            } else {
                mImgOpenClose.setVisibility(View.INVISIBLE);
            }
            Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), drawableId);
            mTextDirectoryName.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }

    }
}
