package com.loopeer.codereaderkt.ui.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.loopeer.codereaderkt.model.DirectoryNode
import com.loopeer.codereaderkt.ui.adapter.DirectoryAdapter
import com.loopeer.codereaderkt.utils.FileCache
import com.loopeer.codereaderkt.utils.FileTypeUtils
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.observables.AsyncOnSubscribe
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.io.File

class DirectoryNavDelegate(private val mRecyclerView: RecyclerView, private val mFileClickListener: FileClickListener) {

    interface FileClickListener {
        fun doOpenFile(node: DirectoryNode?)
    }

    interface LoadFileCallback {
        fun onFileOpenStart()
        fun onFileOpenEnd()
    }

    private val mDirectoryAdapter: DirectoryAdapter = DirectoryAdapter(mRecyclerView.context, mFileClickListener)
    private val mContext: Context = mRecyclerView.context
    private lateinit var mLoadFileCallback: LoadFileCallback
    private val mAllSubscription = CompositeSubscription()

    init {
        setUpRecyclerView()
    }

    fun setLoadFileCallback(loadFileCallback: LoadFileCallback) {
        mLoadFileCallback = loadFileCallback
    }

    fun clearSubscription() {
        mAllSubscription.clear()
    }

    fun resumeDirectoryState(node: DirectoryNode) {
        mDirectoryAdapter.nodeRoot = node
    }

    val directoryNodeInstance: DirectoryNode
        get() = mDirectoryAdapter.nodeRoot

    private fun setUpRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mRecyclerView.adapter = mDirectoryAdapter
    }

    fun updateData(directoryNode: DirectoryNode) {
        mLoadFileCallback.onFileOpenStart()
        mAllSubscription.add(
            Observable.fromCallable{
                val node: DirectoryNode
                var isDirectory=directoryNode.isDirectory
                if (isDirectory) {
                    node = FileCache().getFileDirectory(File(directoryNode.absolutePath!!))!!
                } else {
                    node = directoryNode
                }
                node
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({ mDirectoryAdapter.nodeRoot=it})
                .doOnNext({ checkOpenFirstFile(it) })
                .doOnError {
                    e -> Toast.makeText(mContext, e.message, Toast.LENGTH_SHORT).show()
                }
                .onErrorResumeNext(Observable.empty<DirectoryNode>())
                .doOnCompleted { mLoadFileCallback.onFileOpenEnd() }
                .subscribe())
    }

    private fun checkOpenFirstFile(node: DirectoryNode) {
        if (node.isDirectory) {
            var haveOpen = false
            for (n in node.pathNodes!!) {
                if (FileTypeUtils.isMdFileType(n.name) && n.name.equals("readme.md", ignoreCase = true)) {
                    mFileClickListener.doOpenFile(n)
                    haveOpen = true
                }
            }
            if (!haveOpen) {
                mFileClickListener.doOpenFile(null)
            }
        } else if (!node.isDirectory) {
            mFileClickListener.doOpenFile(node)
        } else {
            mFileClickListener.doOpenFile(null)
        }
    }

    companion object {
        private val TAG = "DirectoryNavDelegate"
    }

}
