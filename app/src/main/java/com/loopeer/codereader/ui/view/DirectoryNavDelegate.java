package com.loopeer.codereader.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.loopeer.codereader.model.DirectoryNode;
import com.loopeer.codereader.ui.adapter.DirectoryAdapter;
import com.loopeer.codereader.utils.FileCache;

public class DirectoryNavDelegate {

    public interface FileClickListener{
        void doOpenFile(DirectoryNode node);
    }

    private RecyclerView mRecyclerView;
    private DirectoryAdapter mDirectoryAdapter;
    private Context mContext;

    public DirectoryNavDelegate(RecyclerView recyclerView, FileClickListener listener) {
        mRecyclerView = recyclerView;
        mContext = recyclerView.getContext();
        mDirectoryAdapter = new DirectoryAdapter(recyclerView.getContext(), listener);
        setUpRecyclerView();
        setTestData();
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mDirectoryAdapter);
        mDirectoryAdapter.setNodeRoot(setTestData());
    }

    private DirectoryNode setTestData() {
        DirectoryNode directoryNode = FileCache.getInstance().getFileDirectoryNode();
        return directoryNode;
    }

}
