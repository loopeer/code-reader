package com.loopeer.codereaderkt.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import com.loopeer.codereaderkt.Navigator
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.db.CoReaderDbHelper
import com.loopeer.codereaderkt.event.DownloadProgressEvent
import com.loopeer.codereaderkt.model.MainHeaderItem
import com.loopeer.codereaderkt.model.Repo
import com.loopeer.codereaderkt.ui.view.ForegroundProgressRelativeLayout
import com.loopeer.codereaderkt.utils.RxBus
import com.loopeer.itemtouchhelperextension.Extension
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription


class MainLatestAdapter(context: Context) : RecyclerViewAdapter<Repo>(context) {

    private val TAG = "MainLatestAdapter"
    private var mAllSubscription: CompositeSubscription

    init {
        mAllSubscription = CompositeSubscription()
    }

    override fun setData(data: List<Repo>) {
        val list = ArrayList<Repo>()
        list.add(Repo())//这一句的作用是什么:数据库中取出文件数据要多一条来显示顶部菜单
        list.addAll(data)
        super.setData(list)
    }


    override fun bindView(var1: Repo, var2: Int, var3: RecyclerView.ViewHolder?) {
        if (var3 is RepoViewHolder) {
            val viewHolder: RepoViewHolder = var3
            val subscription = viewHolder.bind(var1)
            if (subscription != null) {
                mAllSubscription.add(subscription)
            }
            viewHolder.mProgressRelativeLayout.setOnClickListener {
                //可以监听到这里
                if (!var1.isDownloading() && !var1.isUnzip)
                                   Navigator().startCodeReadActivity(context, var1)
            }
            viewHolder.mActionDeleteView.setOnClickListener { doRepoDelete(var3) }//怎么让它们滑动显现出来
            viewHolder.mActionSyncView.setOnClickListener { Navigator().startDownloadRepoService(context, var1) }
        }
        if (var3 is MainHeaderHolder) {
            val viewHolder = var3
            viewHolder.bind()
        }


    }

    private fun doRepoDelete(var3: RecyclerView.ViewHolder) {
        val position = var3.adapterPosition
        val repo = mData[position]
        CoReaderDbHelper.getInstance(context).deleteRepo(java.lang.Long.parseLong(repo.id))
        if (repo.downloadId > 0) Navigator().startDownloadRepoServiceRemove(context, repo.downloadId)
        deleteItem(position)
    }

    fun deleteRepo(repo: Repo) {
        val index = mData.indexOf(repo)
        if (index == -1) return
        deleteItem(index)
    }

    private fun deleteItem(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clearSubscription() {
        mAllSubscription.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val inflater = layoutInflater
        val view: View
        return when (viewType) {
            R.layout.list_item_main_top_header -> {
                view = inflater.inflate(R.layout.list_item_main_top_header, parent, false)
                MainHeaderHolder(view)
            }
            else -> {
                view = inflater.inflate(R.layout.list_item_repo, parent, false)
                RepoViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
            if (position == 0) R.layout.list_item_main_top_header else R.layout.list_item_repo

    class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), Extension {


        var mImgRepoType: ImageView = itemView.findViewById(R.id.img_repo_type)
        var mTextRepoName: TextView = itemView.findViewById(R.id.text_repo_name)
        var mTextRepoTime: TextView = itemView.findViewById(R.id.text_repo_time)
        var mProgressRelativeLayout: ForegroundProgressRelativeLayout = itemView.findViewById(R.id.view_progress_list_repo)
        var mActionDeleteView: View = itemView.findViewById(R.id.view_list_repo_action_delete)
        var mActionSyncView: View = itemView.findViewById(R.id.view_list_repo_action_update)
        var mActionContainer: View = itemView.findViewById(R.id.view_list_repo_action_container)
        var mCloud: View = itemView.findViewById(R.id.img_list_repo_cloud)
        var mLocalPhone: View = itemView.findViewById(R.id.img_list_repo_phone)
        private var mSubscription: Subscription? = null

        fun bind(repo: Repo): Subscription? {
            mImgRepoType.setBackgroundResource(if (repo.isFolder) R.drawable.shape_circle_folder else R.drawable.shape_circle_document)
            mImgRepoType.setImageResource(if (repo.isFolder) R.drawable.ic_repo_white else R.drawable.ic_document_white)
            mTextRepoName.text = repo.name
            mTextRepoTime.text = DateUtils.getRelativeTimeSpanString(itemView.context, repo.lastModify)
            mActionSyncView.visibility = if (repo.isNetRepo()) View.VISIBLE else View.GONE
            mCloud.visibility = if (repo.isNetRepo()) View.VISIBLE else View.GONE
            mLocalPhone.visibility = if (repo.isLocalRepo()) View.VISIBLE else View.GONE
            resetSubscription(repo)

            if (repo.isDownloading()) {
                mProgressRelativeLayout.setInitProgress(repo.factor)
            } else {
                mProgressRelativeLayout.setInitProgress(1f)
            }
            mProgressRelativeLayout.setUnzip(repo.isUnzip)

            return mSubscription
        }

        private fun resetSubscription(repo: Repo) {
            if (mSubscription != null && !mSubscription!!.isUnsubscribed) {
                mSubscription?.unsubscribe()
            }
            mSubscription = RxBus.getInstance()
                    .toObservable()
                    .filter({ o -> o is DownloadProgressEvent })
                    .map({ o -> o as DownloadProgressEvent })
                    .filter({ o -> o.downloadId == repo.downloadId || repo.id == o.repoId })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext({ o -> if (repo.downloadId.equals(0)) repo.downloadId = o.downloadId })
                    .doOnNext({ o -> mProgressRelativeLayout.setProgressCurrent(o.factor) })
                    .filter({ o -> o.factor == 1f })
                    .doOnNext({ o -> repo.isUnzip = o.isUnzip })
                    .doOnNext({ o -> mProgressRelativeLayout.setUnzip(o.isUnzip) })
                    .filter({ o -> o.isUnzip == false })
                    .doOnNext({ repo.downloadId = 0 })
                    .subscribe()
        }

        override fun getActionWidth(): Float = mActionContainer.width.toFloat()
    }


    class MainHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var mMainHeaderAdapter: MainHeaderAdapter = MainHeaderAdapter(itemView.context)
        private var mGridView: GridView = itemView.findViewById(R.id.grid_main)

        init {
            mGridView.adapter = mMainHeaderAdapter
        }

        fun bind() {
            val items = ArrayList<MainHeaderItem>()
            items.add(MainHeaderItem(R.drawable.ic_github, R.string.header_item_github_search,
                    itemView.context.getString(R.string.header_item_github_search_link)))
            items.add(MainHeaderItem(R.drawable.ic_trending, R.string.header_item_trending
                    , itemView.context.getString(R.string.header_item_trending_link)))
            mMainHeaderAdapter.updateData(items)
        }

    }

}