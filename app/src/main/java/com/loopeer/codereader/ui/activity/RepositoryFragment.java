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
import com.loopeer.codereader.ui.adapter.RepositoryAdapter;
import com.loopeer.codereader.ui.decoration.DividerItemDecoration;
import com.loopeer.codereader.ui.fragment.BaseFragment;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RepositoryFragment extends BaseFragment {

    @BindView(R.id.view_recycler)
    RecyclerView mViewRecycler;

    private RepositoryAdapter mRepositoryAdapter;
    private GithubService mGithubService;

    private String mSearchText;

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
    }

    public void setSearchText(String searchText) {
        mSearchText = searchText;
        requestData();
    }

    private void requestData() {
        showProgressLoading("");
        getProgressLoading().setCanceledOnTouchOutside(false);
        registerSubscription(mGithubService.repositories(mSearchText, null, null)
                .filter(baseListResponseResponse -> baseListResponseResponse.isSuccessful())
                .map(baseListResponseResponse -> baseListResponseResponse.body().items)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(repositories -> {
                    mRepositoryAdapter.updateData(repositories);
                    dismissProgressLoading();
                }, throwable -> {
                    throwable.printStackTrace();
                    dismissProgressLoading();
                }));
    }

    @Override
    public void showProgressLoading(String message) {
        mViewRecycler.setVisibility(View.INVISIBLE);
        super.showProgressLoading(message);
    }

    @Override
    public void dismissProgressLoading() {
        mViewRecycler.setVisibility(View.VISIBLE);
        super.dismissProgressLoading();
    }

}
