package com.loopeer.codereader.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.loopeer.codereader.model.DirectoryNode;
import com.loopeer.codereader.ui.adapter.DirectoryAdapter;
import com.loopeer.codereader.utils.FileUtils;

public class DirectoryNavDelegate {

    private RecyclerView mRecyclerView;
    private DirectoryAdapter mDirectoryAdapter;
    private Context mContext;

    public DirectoryNavDelegate(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mContext = recyclerView.getContext();
        mDirectoryAdapter = new DirectoryAdapter(recyclerView.getContext());
        setUpRecyclerView();
        setTestData();
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mDirectoryAdapter);
        //ToDo

        mDirectoryAdapter.setNodeRoot(setTestData());
    }

    private DirectoryNode setTestData() {
        DirectoryNode directoryNode = FileUtils.getFileDirectoryNode(mContext);
        return directoryNode.pathNodes.get(0);
    }

}
