package com.loopeer.codereader.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.loopeer.codereader.model.DirectoryNode;
import com.loopeer.codereader.ui.adapter.DirectoryAdapter;

import java.util.ArrayList;
import java.util.List;

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
        mDirectoryAdapter.updateData(setTestData());
    }

    private List<DirectoryNode> setTestData() {
        List<DirectoryNode> nodes = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            nodes.add(new DirectoryNode("maimRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));n"));
            //maimRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));n
        }
        return nodes;
    }

}
