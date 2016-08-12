package com.loopeer.codereader.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.loopeer.codereader.Navigator;
import com.loopeer.codereader.R;
import com.loopeer.codereader.api.ServiceFactory;
import com.loopeer.codereader.api.service.GithubService;
import com.loopeer.codereader.ui.adapter.RepositoryAdapter;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RepositoryActivity extends BaseActivity {

    @BindView(R.id.view_recycler)
    RecyclerView mViewRecycler;

    private RepositoryAdapter mRepositoryAdapter;
    private GithubService mGithubService;

    private String mSearchText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        parseIntent();

        mRepositoryAdapter = new RepositoryAdapter(this);
        mGithubService = ServiceFactory.getGithubService();

        setupRecyclerView();
        requestData();
    }

    private void parseIntent() {
        mSearchText = getIntent().getStringExtra(Navigator.EXTRA_SEARCH_TEXT);
    }

    private void requestData() {
        registerSubscription(mGithubService.repositories(mSearchText, null, null)
                .filter(baseListResponseResponse -> baseListResponseResponse.isSuccessful())
                .map(baseListResponseResponse -> baseListResponseResponse.body().items)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(repositories -> {
                    mRepositoryAdapter.updateData(repositories);
                }));
    }

    private void setupRecyclerView() {
        mViewRecycler.setLayoutManager(new LinearLayoutManager(this));
        mViewRecycler.setAdapter(mRepositoryAdapter);
    }

}
