package com.loopeer.codereaderkt.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.loopeer.codereaderkt.model.DirectoryNode;
import com.loopeer.codereaderkt.ui.adapter.DirectoryAdapter;
import com.loopeer.codereaderkt.utils.FileCache;
import com.loopeer.codereaderkt.utils.FileTypeUtils;

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

    public interface LoadFileCallback{
        void onFileOpenStart();
        void onFileOpenEnd();
    }

    private RecyclerView mRecyclerView;
    private DirectoryAdapter mDirectoryAdapter;
    private Context mContext;
    private FileClickListener mFileClickListener;
    private LoadFileCallback mLoadFileCallback;
    private final CompositeSubscription mAllSubscription = new CompositeSubscription();

    public DirectoryNavDelegate(RecyclerView recyclerView, FileClickListener listener) {
        mRecyclerView = recyclerView;
        mContext = recyclerView.getContext();
        mFileClickListener = listener;
        mDirectoryAdapter = new DirectoryAdapter(recyclerView.getContext(), listener);
        setUpRecyclerView();
    }

    public void setLoadFileCallback(LoadFileCallback loadFileCallback) {
        mLoadFileCallback = loadFileCallback;
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
        mLoadFileCallback.onFileOpenStart();
        mAllSubscription.add(
                Observable.fromCallable(() -> {
                    DirectoryNode node;
                    if (directoryNode.isDirectory()) {
                        node = new FileCache().getFileDirectory(new File(directoryNode.getAbsolutePath()));
                    } else {
                        node = directoryNode;
                    }
                    return node;
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(mDirectoryAdapter::setNodeRoot)
                        .doOnNext(this::checkOpenFirstFile)
                        .doOnError(e -> Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show())
                        .onErrorResumeNext(Observable.empty())
                        .doOnCompleted(() -> mLoadFileCallback.onFileOpenEnd())
                        .subscribe());
    }

    private void checkOpenFirstFile(DirectoryNode node) {
        if (node.isDirectory() && node.pathNodes != null) {
            boolean haveOpen = false;
            for (DirectoryNode n : node.pathNodes) {
                if (FileTypeUtils.isMdFileType(n.name) && n.name.equalsIgnoreCase("readme.md")) {
                    mFileClickListener.doOpenFile(n);
                    haveOpen = true;
                }
            }
            if (!haveOpen) {
                mFileClickListener.doOpenFile(null);
            }
        } else if (!node.isDirectory()) {
            mFileClickListener.doOpenFile(node);
        } else {
            mFileClickListener.doOpenFile(null);
        }
    }

}
