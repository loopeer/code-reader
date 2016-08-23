package com.loopeer.codereader.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.loopeer.codereader.R;
import com.loopeer.codereader.event.DownloadRepoMessageEvent;
import com.loopeer.codereader.ui.view.ProgressLoading;
import com.loopeer.codereader.utils.RxBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class BaseActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @Nullable
    @BindView(R.id.view_coordinator_container)
    CoordinatorLayout mCoordinatorContainer;

    private final CompositeSubscription mAllSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerSubscription(
                RxBus.getInstance()
                        .toObservable()
                        .filter(o -> o instanceof DownloadRepoMessageEvent)
                        .map(o -> (DownloadRepoMessageEvent) o)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(o -> showMessage(o.getMessage()))
                        .subscribe());
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            onSetupActionBar(getSupportActionBar());
        }

        String title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }
    }

    protected void onSetupActionBar(ActionBar actionBar) {
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    protected void registerSubscription(Subscription subscription) {
        mAllSubscription.add(subscription);
    }

    protected void unregisterSubscription(Subscription subscription) {
        mAllSubscription.remove(subscription);
    }

    protected void clearSubscription() {
        mAllSubscription.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearSubscription();
        if (isProgressShow() && mProgressLoading != null) {
            dismissProgressLoading();
            mProgressLoading = null;
        }
    }

    private ProgressLoading mProgressLoading;
    private ProgressLoading mUnBackProgressLoading;
    private boolean progressShow;

    public void showProgressLoading(int resId) {
        showProgressLoading(getString(resId));
    }

    public void showProgressLoading(String message) {
        if (mProgressLoading == null) {
            mProgressLoading = new ProgressLoading(this, R.style.ProgressLoadingTheme);
            mProgressLoading.setCanceledOnTouchOutside(true);
            mProgressLoading.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    progressShow = false;
                }
            });
        }
        if (!TextUtils.isEmpty(message)) {
            mProgressLoading.setMessage(message);
        } else {
            mProgressLoading.setMessage(null);
        }
        progressShow = true;
        mProgressLoading.show();
    }

    public boolean isProgressShow() {
        return progressShow;
    }

    public void dismissProgressLoading() {
        if (mProgressLoading != null && !isFinishing()) {
            progressShow = false;
            mProgressLoading.dismiss();
        }
    }

    public void showUnBackProgressLoading(int resId) {
        showUnBackProgressLoading(getString(resId));
    }

    // 按返回键不可撤销的
    public void showUnBackProgressLoading(String message) {
        if (mUnBackProgressLoading == null) {
            mUnBackProgressLoading = new ProgressLoading(this, R.style.ProgressLoadingTheme) {
                @Override
                public void onBackPressed() {
                }
            };
        }
        if (!TextUtils.isEmpty(message)) {
            mUnBackProgressLoading.setMessage(message);
        } else {
            mUnBackProgressLoading.setMessage(null);
        }
        mUnBackProgressLoading.show();
    }

    public void dismissUnBackProgressLoading() {
        if (mUnBackProgressLoading != null && !isFinishing()) {
            mUnBackProgressLoading.dismiss();
        }
    }

    public void hideSoftInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()){
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showMessage(String message) {
        if (mCoordinatorContainer != null)
            Snackbar.make(mCoordinatorContainer, message, Snackbar.LENGTH_SHORT).show();
    }
}
