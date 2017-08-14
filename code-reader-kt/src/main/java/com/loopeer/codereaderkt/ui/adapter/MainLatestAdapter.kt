package com.loopeer.codereaderkt.ui.adapter

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import com.loopeer.codereaderkt.Navigator
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.model.MainHeaderItem
import com.loopeer.codereaderkt.model.Repo
import com.loopeer.codereaderkt.ui.view.ForegroundProgressRelativeLayout
import com.loopeer.codereaderkt.utils.RxBus
import com.loopeer.itemtouchhelperextension.Extension
import rx.Subscription
import rx.subscriptions.CompositeSubscription
import java.util.ArrayList


class MainLatestAdapter : RecyclerViewAdapter<Repo> {
    private val TAG = "MainLatestAdapter"

    constructor(context: Context) : super(context = context) {

    }

    private val mAllSubscription = CompositeSubscription()

    //kotlin如果要继承父类方法，父类对应方法需open修饰符
    override fun setData(data: List<Repo>?) {
        val list = ArrayList<Repo>()
        list.add(null!!)
        list.addAll(data!!)
        super.setData(list)
    }

    override fun bindView(var1: Repo, var2: Int, var3: RecyclerView.ViewHolder) {
        /*if (var3 is RepoViewHolder) {
            val viewHolder = var3 as RepoViewHolder
            val subscription = viewHolder.bind(var1)
            if (subscription != null) {
                mAllSubscription.add(subscription!!)
            }
            *//*viewHolder.mProgressRelativeLayout?.setOnClickListener({ view ->
             *//**//*   if (!var1.isDownloading() && !var1.isUnzip)
                    Navigator.startCodeReadActivity(getContext(), var1)*//**//*
            })
            viewHolder.mActionDeleteView?.setOnClickListener({*//**//* view -> doRepoDelete(var3) *//**//*})
            viewHolder.mActionSyncView?.setOnClickListener({*//**//* view -> Navigator.startDownloadRepoService(getContext(), var1)*//**//* }
            )*//*//onClickListener
        }
        if (var3 is MainHeaderHolder) {//绑定顶部菜单
            val viewHolder = var3 as MainHeaderHolder
            viewHolder.bind()
        }*/
        val viewHolder = var3 as MainHeaderHolder
        viewHolder.bind()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val inflater = getLayoutInflater()
        val view: View
        when (viewType) {//顶部菜单
            R.layout.list_item_main_top_header -> {
                view = inflater.inflate(R.layout.list_item_main_top_header, parent, false)
                return MainHeaderHolder(view)
            }
            else -> {//repo仓库
                view = inflater.inflate(R.layout.list_item_repo, parent, false)
                return RepoViewHolder(view)//暂时先屏蔽
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return return R.layout.list_item_main_top_header
    }


    inner class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), Extension {


        internal var mImgRepoType: ImageView? = null
        internal var mTextRepoName: TextView? = null
        internal var mTextRepoTime: TextView? = null
        internal var mProgressRelativeLayout: ForegroundProgressRelativeLayout? = null
        internal var mActionDeleteView: View? = null
        internal var mActionSyncView: View? = null
        internal var mActionContainer: View? = null
        internal var mCloud: View? = null
        internal var mLocalPhone: View? = null

        internal var mSubscription: Subscription? = null

        fun bind(repo: Repo): Subscription {

            mImgRepoType!!.setBackgroundResource(if (repo.isFolder) R.drawable.shape_circle_folder else R.drawable.shape_circle_document)
            mImgRepoType!!.setImageResource(if (repo.isFolder) R.drawable.ic_repo_white else R.drawable.ic_document_white)
            mTextRepoName!!.text = repo.name
            mTextRepoTime!!.text = DateUtils.getRelativeTimeSpanString(itemView.context, repo.lastModify)
            mActionSyncView!!.visibility = if (repo.isNetRepo()) View.VISIBLE else View.GONE
            mCloud!!.visibility = if (repo.isNetRepo()) View.VISIBLE else View.GONE
            mLocalPhone!!.visibility = if (repo.isLocalRepo()) View.VISIBLE else View.GONE
            resetSubscription(repo)
            /*if (repo.isDownloading()) {
                mProgressRelativeLayout!!.setInitProgress(repo.factor)
            } else {
                mProgressRelativeLayout!!.setInitProgress(1f)
            }
            mProgressRelativeLayout!!.setUnzip(repo.isUnzip)*/
            return mSubscription!!
        }

        private fun resetSubscription(repo: Repo) {
            /*if (mSubscription != null && !mSubscription!!.isUnsubscribed) {
                mSubscription!!.unsubscribe()
            }
            mSubscription = RxBus.getInstance()
                    .toObservable()
                    .filter({ o -> o is DownloadProgressEvent })
                    .map({ o -> o as DownloadProgressEvent })
                    .filter({ o -> o.downloadId == repo.downloadId || repo.id == o.repoId })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext({ o -> if (repo.downloadId == 0) repo.downloadId = o.downloadId })
                    .doOnNext({ o -> mProgressRelativeLayout!!.setProgressCurrent(o.factor) })
                    .filter({ o -> o.factor == 1f })
                    .doOnNext({ o -> repo.isUnzip = o.isUnzip })
                    .doOnNext({ o -> mProgressRelativeLayout!!.setUnzip(o.isUnzip) })
                    .filter({ o -> o.isUnzip == false })
                    .doOnNext({ o -> repo.downloadId = 0 })
                    .subscribe()*/
        }

        override fun getActionWidth(): Float {
            return mActionContainer!!.width.toFloat()
        }

    }

    internal inner class MainHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var mGridView: GridView? = null
        private val mMainHeaderAdapter: MainHeaderAdapter

        init {
            mGridView = itemView.findViewById(R.id.grid_main)
            mMainHeaderAdapter = MainHeaderAdapter(itemView.context)
            mGridView!!.adapter = mMainHeaderAdapter
        }

        fun bind() {
            val items = ArrayList<MainHeaderItem>()
            items.add(MainHeaderItem(R.drawable.ic_github, R.string.header_item_github_search, itemView.context.getString(R.string.header_item_github_search_link)))
            items.add(MainHeaderItem(R.drawable.ic_trending, R.string.header_item_trending, itemView.context.getString(R.string.header_item_trending_link)))
            mMainHeaderAdapter.updateData(items)
        }
    }

}


