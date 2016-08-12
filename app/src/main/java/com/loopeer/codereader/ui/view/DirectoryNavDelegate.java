package com.loopeer.codereader.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.loopeer.codereader.model.DirectoryNode;
import com.loopeer.codereader.ui.adapter.DirectoryAdapter;
import com.loopeer.codereader.utils.FileCache;
import com.loopeer.codereader.utils.FileUtils;

import java.io.File;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class DirectoryNavDelegate {
    private static final String TAG = "DirectoryNavDelegate";

    public interface FileClickListener {
        void doOpenFile(DirectoryNode node);
    }

    private RecyclerView mRecyclerView;
    private DirectoryAdapter mDirectoryAdapter;
    private Context mContext;
    private FileClickListener mFileClickListener;
    private final CompositeSubscription mAllSubscription = new CompositeSubscription();

    public DirectoryNavDelegate(RecyclerView recyclerView, FileClickListener listener) {
        mRecyclerView = recyclerView;
        mContext = recyclerView.getContext();
        mFileClickListener = listener;
        mDirectoryAdapter = new DirectoryAdapter(recyclerView.getContext(), listener);
        setUpRecyclerView();
    }

    public void clearSubscription() {
        mAllSubscription.clear();
    }

    public void resumeDirectoryState(DirectoryNode node) {
        mDirectoryAdapter.setNodeRoot(node);
    }

    public DirectoryNode getDirectoryNodeInstance() {
        return mDirectoryAdapter.getNodeRoot();
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mDirectoryAdapter);
    }

    public void updateData(DirectoryNode directoryNode) {
        mAllSubscription.add(
                Observable.fromCallable(() -> {
                    DirectoryNode node;
                    Log.e(TAG, "1111");

                    if (directoryNode.isDirectory) {
                        node = FileCache.getFileDirectory(new File(directoryNode.absolutePath));
                    } else {
                        node = directoryNode;
                    }
                    return node;
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(mDirectoryAdapter::setNodeRoot)
                        .doOnNext(this::checkOpenFirstFile)
                        .doOnError(e -> Log.d(TAG, "error: " + e.toString()))
                        .subscribe());
    }

    private void checkOpenFirstFile(DirectoryNode node) {
        if (node.isDirectory && node.pathNodes != null) {
            boolean haveOpen = false;
            for (DirectoryNode n : node.pathNodes) {
                if (FileUtils.isMdFileType(n.name)) {
                    mFileClickListener.doOpenFile(n);
                    haveOpen = true;
                }
            }
            if (!haveOpen) {
                mFileClickListener.doOpenFile(null);
            }
        } else if (!node.isDirectory) {
            mFileClickListener.doOpenFile(node);
        } else {
            mFileClickListener.doOpenFile(null);
        }
    }

}
