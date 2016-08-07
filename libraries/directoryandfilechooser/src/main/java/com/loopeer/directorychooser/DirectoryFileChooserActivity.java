package com.loopeer.directorychooser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ViewAnimator;

import java.io.File;
import java.util.List;
import java.util.Stack;

public class DirectoryFileChooserActivity extends AppCompatActivity implements DirectoryFileAdapter.OnNodeSelectListener, DirectoryFileAdapter.OnDirectoryClickListener {

    private RecyclerView mRecyclerView;
    private ViewAnimator mViewAnimator;
    private DirectoryFileAdapter mDirectoryFileAdapter;
    private Stack<FileNod> mSelectedNodeStack;
    private FileNod preSelectedNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_file_chooser);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.view_recycler);
        mViewAnimator = (ViewAnimator) findViewById(R.id.animator_recycler_content);
        mSelectedNodeStack = new Stack<>();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        mDirectoryFileAdapter = new DirectoryFileAdapter(this);
        mDirectoryFileAdapter.setItemClickListener(this);
        mDirectoryFileAdapter.setNodeSelectListener(this);
        mRecyclerView.setAdapter(mDirectoryFileAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecorationChooser(this));
        initData();
    }

    private void initData() {
        FileNod rootNode = DirectoryUtils.getRootNode(this);
        preSelectedNode = rootNode;
        updateContent(rootNode);
    }

    @Override
    public void onNodeSelected(FileNod node) {

    }

    @Override
    public void onDirectoryClick(FileNod node) {
        if (node.isFolder) {
            updateDataWithNode(node);
        }
    }

    private void updateDataWithNode(FileNod node) {
        FileNod selectNode = DirectoryUtils.getFileDirectory(new File(node.absolutePath));
        pushToSelectedStack(preSelectedNode);
        preSelectedNode = selectNode;
        updateContent(selectNode);
    }

    private void pushToSelectedStack(FileNod selectNode) {
        mSelectedNodeStack.push(selectNode);
    }

    private void popSelectedStack() {
        FileNod node = mSelectedNodeStack.pop();
        updateContent(node);
    }

    private void updateContent(FileNod selectNode) {
        List<FileNod> nods = selectNode.childNodes;
        if (nods == null || nods.isEmpty()) {
            showEmpty();
        } else {
            mDirectoryFileAdapter.updateData(nods);
            showContent();
        }
    }

    @Override
    public void onBackPressed() {
        if (mSelectedNodeStack.isEmpty()) {
            super.onBackPressed();
        } else {
            popSelectedStack();
        }
    }

    private void showContent() {
        mViewAnimator.setDisplayedChild(0);
    }

    private void showEmpty() {
        mViewAnimator.setDisplayedChild(1);
    }

    private void showProgress() {
        mViewAnimator.setDisplayedChild(2);
    }
}
