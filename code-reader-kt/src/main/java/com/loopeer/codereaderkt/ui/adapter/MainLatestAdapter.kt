package com.loopeer.codereaderkt.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.model.MainHeaderItem
import com.loopeer.codereaderkt.model.Repo
import com.loopeer.codereaderkt.ui.view.ForegroundProgressRelativeLayout
import com.loopeer.itemtouchhelperextension.Extension
import rx.Subscription
import rx.subscriptions.CompositeSubscription


class MainLatestAdapter(context: Context) : RecyclerViewAdapter<Repo>(context) {

    private val TAG = "MainLatestAdapter"
    init {
        Log.d("MainLatestAdapterLog"," init")
    }

    private val mAllSubscription: CompositeSubscription? = null

    override fun setData(data: List<Repo>) {
        var list = ArrayList<Repo>()
        list.add(null!!)
        list.addAll(data)
        super.setData(data)
    }


    override fun bindView(var1: Repo?, var2: Int, var3: RecyclerView.ViewHolder?) {
        Log.d("MainLatestAdapterLog","bindView"+var3)
        if (var3 is RepoViewHolder) {
            Log.d("MainLatestAdapterLog","bindView : RepoView")
            val viewHolder: RepoViewHolder = var3
            val subscription = viewHolder.bind(var1!!)
            if (subscription != null) {
                mAllSubscription!!.add(subscription)
            }
            viewHolder.mProgressRelativeLayout!!.setOnClickListener {
                /*view ->
                               if (!var1.isDownloading() && !var1.isUnzip)
                                   Navigator.startCodeReadActivity(context, var1)*/
            }
            viewHolder.mActionDeleteView!!.setOnClickListener { /*view -> doRepoDelete(var3)*/ }
            viewHolder.mActionSyncView!!.setOnClickListener { /*view -> Navigator.startDownloadRepoService(context, var1)*/ }
        }
        if (var3 is MainHeaderHolder){
            Log.d("MainLatestAdapterLog","bindView : HeaderView")
            var3.bind()
        }


    }

    fun clearSubscription() {
        mAllSubscription?.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
     var inflater = layoutInflater
        val view: View
        when (viewType) {
            R.layout.list_item_main_top_header -> {
                view = inflater.inflate(R.layout.list_item_main_top_header, parent, false)
                return MainHeaderHolder(view)
            }
            else -> {
                view = inflater.inflate(R.layout.list_item_repo, parent, false)
                return RepoViewHolder(view)
            }
        }
    }


    class RepoViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), Extension {


        var mImgRepoType: ImageView? = null
        var mTextRepoName: TextView? = null
        var mTextRepoTime: TextView? = null
        var mProgressRelativeLayout: ForegroundProgressRelativeLayout? = null
        var mActionDeleteView: View? = null
        var mActionSyncView: View? = null
        var mActionContainer: View? = null
        var mCloud: View? = null
        var mLocalPhone: View? = null
        var mSubscription: Subscription? = null

        init {
            mImgRepoType = itemView!!.findViewById(R.id.img_repo_type)
            mTextRepoName = itemView.findViewById(R.id.text_repo_name)
            mTextRepoTime = itemView.findViewById(R.id.text_repo_time)
            mProgressRelativeLayout = itemView.findViewById(R.id.view_progress_list_repo)
            mActionDeleteView = itemView.findViewById(R.id.view_list_repo_action_delete)
            mActionSyncView = itemView.findViewById(R.id.view_list_repo_action_update)
            mActionContainer = itemView.findViewById(R.id.view_list_repo_action_container)
            mCloud = itemView.findViewById(R.id.img_list_repo_cloud)
            mLocalPhone = itemView.findViewById(R.id.img_list_repo_phone)
        }

        fun bind(repo: Repo): Subscription? {
            mImgRepoType!!.setBackgroundResource(if (repo.isFolder) R.drawable.shape_circle_folder else R.drawable.shape_circle_document)
            mImgRepoType!!.setImageResource(if (repo.isFolder) R.drawable.ic_repo_white else R.drawable.ic_document_white)
            mTextRepoName!!.text = repo.name
            mTextRepoTime!!.text = DateUtils.getRelativeTimeSpanString(itemView.context, repo.lastModify)
            mActionSyncView!!.visibility = if (repo.isNetRepo()) View.VISIBLE else View.GONE
            mCloud!!.visibility = if (repo.isNetRepo()) View.VISIBLE else View.GONE
            mLocalPhone!!.visibility = if (repo.isLocalRepo()) View.VISIBLE else View.GONE
            resetSubscription(repo)

            if (repo.isDownloading()) {
                mProgressRelativeLayout!!.setInitProgress(repo.factor)
            } else {
                mProgressRelativeLayout!!.setInitProgress(1f)
            }
            mProgressRelativeLayout!!.setUnzip(repo.isUnzip)

            return mSubscription
        }

        fun resetSubscription(repo: Repo) {
            if (mSubscription != null && !mSubscription!!.isUnsubscribed) {
                mSubscription!!.unsubscribe()
            }
            /*mSubscription = RxBus.getInstance()
                    .toObservable()
                    .filter({ o -> o is DownloadProgressEvent })
                    .map({ o -> o as DownloadProgressEvent })
                    .filter({ o -> o.downloadId == repo.downloadId || repo.id == o.repoId })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext({ o -> if (repo.downloadId == 0) repo.downloadId = o.downloadId })
                    .doOnNext({ o -> mProgressRelativeLayout.setProgressCurrent(o.factor) })
                    .filter({ o -> o.factor == 1f })
                    .doOnNext({ o -> repo.isUnzip = o.isUnzip })
                    .doOnNext({ o -> mProgressRelativeLayout.setUnzip(o.isUnzip) })
                    .filter({ o -> o.isUnzip == false })
                    .doOnNext({ o -> repo.downloadId = 0 })
                    .subscribe()*/
        }

        override fun getActionWidth(): Float {
            return mActionContainer!!.width.toFloat()
        }
    }


    class MainHeaderHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        private var mMainHeaderAdapter: MainHeaderAdapter? = null
        private lateinit var mGridView: GridView

        init {
            if (itemView != null) {
                mGridView = itemView.findViewById(R.id.grid_main)
            }
            mMainHeaderAdapter = MainHeaderAdapter(itemView!!.context)
            mGridView.adapter = mMainHeaderAdapter
        }

        fun bind() {
            var items = ArrayList<MainHeaderItem>()
            Log.d("MainHeaderViewHolderLog","bind")
            items.add(MainHeaderItem(R.drawable.ic_github, R.string.header_item_github_search,
                    itemView.context.getString(R.string.header_item_github_search_link)))
            items.add(MainHeaderItem(R.drawable.ic_trending, R.string.header_item_trending
                    , itemView.context.getString(R.string.header_item_trending_link)))
            mMainHeaderAdapter!!.updateData(items)
        }

    }

}