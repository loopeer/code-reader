package com.loopeer.directorychooser;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewAnimator;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class DirectoryFileChooserActivity extends AppCompatActivity implements DirectoryFileAdapter.OnNodeSelectListener, DirectoryFileAdapter.OnDirectoryClickListener {

    private RecyclerView mRecyclerView;
    private ViewAnimator mViewAnimator;
    private TextView mTextSelectedPath;

    private DirectoryFileAdapter mDirectoryFileAdapter;
    private LinkedList<FileNod> mSelectedNodeStack;
    private FileNod preSelectedNode;
    private FileNod currentSelectedNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_file_chooser);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.view_recycler);
        mViewAnimator = (ViewAnimator) findViewById(R.id.animator_recycler_content);
        mTextSelectedPath = (TextView) findViewById(R.id.text_chooser_path);
        mSelectedNodeStack = new LinkedList<>();
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
        /*FileNod rootNode = DirectoryUtils.getRootNode(this);
        currentSelectedNode = rootNode;
        updateLinkedClick();
        updateContent(rootNode);*/
        updateDataWithNode(DirectoryUtils.getFileDirectory(Environment.getExternalStorageDirectory()));
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

    private void updateLinkedClick() {
        StringBuilder sb = new StringBuilder();
        int count = mSelectedNodeStack.size();
        int[] nodeStartPos = new int[count + 1];
        int[] nodeEndPos = new int[count + 1];
        for (int i = count - 1; i >= 0; i--) {
            FileNod node = mSelectedNodeStack.get(i);
            appendPath(sb, nodeStartPos, nodeEndPos, node, count - i - 1);
        }
        appendPath(sb, nodeStartPos, nodeEndPos, currentSelectedNode, count);
        SpannableString spannableString = new SpannableString(sb.toString());
        for (int i = 0; i < count + 1; i++) {
            final int finalI = i;
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    popToPosition(finalI);
                }
            }, nodeStartPos[i], nodeEndPos[i], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mTextSelectedPath.setText(spannableString);
        mTextSelectedPath.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void appendPath(StringBuilder sb, int[] nodeStartPos, int[] nodeEndPos, FileNod node, int i) {
        int size = node.name.length() + 1;
        nodeStartPos[i] = sb.length();
        nodeEndPos[i] = sb.length() + size;
        sb.append(node.name);
        sb.append(">");
    }

    private void popToPosition(int position) {
        FileNod node = null;
        for (int i = mSelectedNodeStack.size(); i > position; i--) {
            node = mSelectedNodeStack.pop();
        }
        preSelectedNode = mSelectedNodeStack.peek();
        currentSelectedNode = node;
        updateLinkedClick();
        updateContent(node);
    }

    private void updateDataWithNode(FileNod node) {
        FileNod selectNode = DirectoryUtils.getFileDirectory(new File(node.absolutePath));
        if (currentSelectedNode != null) pushToSelectedStack(currentSelectedNode);
        currentSelectedNode = selectNode;
        updateLinkedClick();
        updateContent(selectNode);
    }

    private void pushToSelectedStack(FileNod selectNode) {
        mSelectedNodeStack.push(selectNode);
    }

    private void popSelectedStack() {
        currentSelectedNode = mSelectedNodeStack.pop();
        preSelectedNode = mSelectedNodeStack.peek();
        updateLinkedClick();
        updateContent(currentSelectedNode);
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
