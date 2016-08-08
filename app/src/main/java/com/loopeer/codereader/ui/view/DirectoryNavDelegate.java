package com.loopeer.codereader.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.loopeer.codereader.model.DirectoryNode;
import com.loopeer.codereader.ui.adapter.DirectoryAdapter;
import com.loopeer.codereader.utils.FileCache;
import com.loopeer.codereader.utils.FileUtils;

import java.io.File;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class DirectoryNavDelegate {

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

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mDirectoryAdapter);
    }

    public void updateData(DirectoryNode directoryNode, DirectoryNode selectNode) {
        mAllSubscription.add(
                Observable.create(new Observable.OnSubscribe<DirectoryNode>() {
                    @Override
                    public void call(Subscriber<? super DirectoryNode> subscriber) {
                        DirectoryNode node;
                        if (directoryNode.isDirectory) {
                            node = FileCache.getFileDirectory(new File(directoryNode.absolutePath));
                        } else {
                            node = directoryNode;
                        }
                        subscriber.onNext(node);
                        subscriber.onCompleted();
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(mDirectoryAdapter::setNodeRoot)
                        .filter(o -> selectNode == null)
                        .doOnNext(this::checkOpenFirstFile)
                        .subscribe()
        );
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
