package com.loopeer.codereaderkt.ui.activity

import android.os.Build
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.api.ServiceFactory
import com.loopeer.codereaderkt.api.service.GithubService
import com.loopeer.codereaderkt.model.Repository
import com.loopeer.codereaderkt.ui.adapter.RepositoryAdapter
import com.loopeer.codereaderkt.ui.decoration.DividerItemDecoration
import com.loopeer.codereaderkt.ui.fragment.BaseFragment
import com.loopeer.codereaderkt.utils.PageLinkParser
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class RepositoryFragment : BaseFragment() {
    //TODO打开时会崩溃，未找到原因； 原因是BaseFragment继承的Fragment应该引入v4.Fragment而不是app.Fragment

    private val PAGE_SIZE = 10
    private lateinit var mViewRecycler: RecyclerView

    private lateinit var mRepositoryAdapter: RepositoryAdapter
    private lateinit var mGithubService: GithubService

    private lateinit var mSearchText: String

    private lateinit var mRepositories: ArrayList<Repository>

    private lateinit var mPageLinkParser: PageLinkParser

    private var mIsLoading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRepositories = ArrayList()
        mRepositoryAdapter = RepositoryAdapter(activity)
        mGithubService = ServiceFactory().getGithubService()
    }

    override fun onCreateView(inflater: LayoutInflater?, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.activity_search_result, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewRecycler = view.findViewById<RecyclerView>(R.id.view_recycler)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        mViewRecycler.layoutManager = LinearLayoutManager(activity)
        mViewRecycler.adapter = mRepositoryAdapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mViewRecycler.addItemDecoration(DividerItemDecoration(context))
        }

        mViewRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                if (layoutManager.findLastVisibleItemPosition() == mRepositoryAdapter.itemCount - 1 && isHasMore()) {
                    if (!mIsLoading)
                        requestData(mPageLinkParser.next)
                }
            }
        })
    }

    fun setSearchText(searchText: String) {
        mSearchText = searchText
        requestData(1)
    }

    fun isHasMore(): Boolean = mPageLinkParser.next != 0 && mPageLinkParser.remain != 0

    private fun requestData(page: Int) {
        mIsLoading = true

        if (page == 1) {
            mRepositories.clear()
            showProgressLoading("")
        }

        registerSubscription(mGithubService.repositories(mSearchText, null.toString(), null.toString(), page, PAGE_SIZE)
                .filter { baseListResponseResponse -> baseListResponseResponse.isSuccessful }
                .map<List<Repository>> { baseListResponseResponse ->
                    mPageLinkParser = PageLinkParser(baseListResponseResponse)
                    baseListResponseResponse.body().items
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ repositories ->
                    mRepositories.addAll(repositories)
                    mRepositoryAdapter.setHasMore(isHasMore())
                    mRepositoryAdapter.updateData(mRepositories)
                    dismissProgressLoading()

                    mIsLoading = false
                }) { throwable ->
                    throwable.printStackTrace()
                    dismissProgressLoading()

                    mIsLoading = false
                })
    }

    override fun showProgressLoading(message: String) {
        mViewRecycler.visibility = View.INVISIBLE
        super.showProgressLoading(message)
        getProgressLoaing()?.setCanceledOnTouchOutside(false)
    }

    override fun dismissProgressLoading() {
        mViewRecycler.visibility = View.VISIBLE
        super.dismissProgressLoading()
    }


}