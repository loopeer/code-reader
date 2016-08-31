package com.loopeer.codereader.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopeer.codereader.R;
import com.loopeer.codereader.api.ServiceFactory;
import com.loopeer.codereader.api.service.GithubService;
import com.loopeer.codereader.model.Repository;
import com.loopeer.codereader.ui.adapter.RepositoryAdapter;
import com.loopeer.codereader.ui.decoration.DividerItemDecoration;
import com.loopeer.codereader.ui.fragment.BaseFragment;
import com.loopeer.codereader.utils.PageLinkParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RepositoryFragment extends BaseFragment {

    private static final int PAGE_SIZE = 10;

    @BindView(R.id.view_recycler)
    RecyclerView mViewRecycler;

    private RepositoryAdapter mRepositoryAdapter;
    private GithubService mGithubService;

    private String mSearchText;

    private List<Repository> mRepositories = new ArrayList<>();

    private PageLinkParser mPageLinkParser;

    private boolean mIsLoading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRepositoryAdapter = new RepositoryAdapter(getActivity());
        mGithubService = ServiceFactory.getGithubService();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_search_result, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mViewRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mViewRecycler.setAdapter(mRepositoryAdapter);
        mViewRecycler.addItemDecoration(new DividerItemDecoration(getContext()));

        mViewRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager.findLastVisibleItemPosition() == mRepositoryAdapter.getItemCount() - 1 && isHasMore()) {
                    if (!mIsLoading)
                        requestData(mPageLinkParser.getNext());
                }
            }
        });
    }

    public void setSearchText(String searchText) {
        mSearchText = searchText;
        requestData(1);
    }

    public boolean isHasMore() {
        if (mPageLinkParser != null && mPageLinkParser.getNext() != 0 && mPageLinkParser.getRemain() != 0)
            return true;
        return false;
    }

    private void requestData(int page) {
        mIsLoading = true;

        if (page == 1) {
            mRepositories.clear();
            showProgressLoading("");
        }

        registerSubscription(mGithubService.repositories(mSearchText, null, null, page, PAGE_SIZE)
                .filter(baseListResponseResponse -> baseListResponseResponse.isSuccessful())
                .map(baseListResponseResponse -> {
                    mPageLinkParser = new PageLinkParser(baseListResponseResponse);
                    return baseListResponseResponse.body().items;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(repositories -> {
                    mRepositories.addAll(repositories);
                    mRepositoryAdapter.setHasMore(isHasMore());
                    mRepositoryAdapter.updateData(mRepositories);
                    dismissProgressLoading();

                    mIsLoading = false;
                }, throwable -> {
                    throwable.printStackTrace();
                    dismissProgressLoading();

                    mIsLoading = false;
                }));
    }

    @Override
    public void showProgressLoading(String message) {
        mViewRecycler.setVisibility(View.INVISIBLE);
        super.showProgressLoading(message);
        getProgressLoading().setCanceledOnTouchOutside(false);
    }

    @Override
    public void dismissProgressLoading() {
        mViewRecycler.setVisibility(View.VISIBLE);
        super.dismissProgressLoading();
    }

}
