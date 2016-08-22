package com.loopeer.codereader.ui.activity;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.loopeer.codereader.Navigator;
import com.loopeer.codereader.R;
import com.loopeer.codereader.event.DownloadRepoMessageEvent;
import com.loopeer.codereader.ui.view.NestedScrollWebView;
import com.loopeer.codereader.utils.DownloadUrlParser;
import com.loopeer.codereader.utils.RxBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

public class SimpleWebActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = "SimpleWebActivity";

    @BindView(R.id.web_content)
    NestedScrollWebView mWebContent;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.progress_bar_web)
    ProgressBar mProgressBar;
    private SearchView mSearchView;

    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_web);
        ButterKnife.bind(this);

        initWeb();
        parseIntent();
        registerSubscription(
                RxBus.getInstance()
                        .toObservable()
                        .filter(o -> o instanceof DownloadRepoMessageEvent)
                        .map(o -> (DownloadRepoMessageEvent)o)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(o -> showMessage(o.getMessage()))
                        .subscribe());
    }

    private void initWeb() {
        mWebContent.getSettings().setJavaScriptEnabled(true);
        mWebContent.getSettings().setDomStorageEnabled(true);
        mWebContent.getSettings().setGeolocationEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebContent.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mSearchView.setQuery(url, true);
                return true;
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                mSearchView.setQuery(String.valueOf(request.getUrl()), true);
                return true;
            }
        });
        mWebContent.setWebChromeClient(new WebChromeClient());
    }

    private void parseIntent() {
        Intent intent = getIntent();
        mUrl = intent.getStringExtra(Navigator.EXTRA_WEB_URL);
        String htmlString = intent.getStringExtra(Navigator.EXTRA_HTML_STRING);
        if (mUrl == null) mUrl = intent.getDataString();
        if (htmlString != null) loadData(htmlString);
    }

    private void loadData(String htmlString) {
        mWebContent.loadData(htmlString, "text/html", "utf-8");
    }

    private void loadUrl(String webUrl) {
        mWebContent.loadUrl(webUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web_input, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem inputView = menu.findItem(R.id.action_web_input);
        mSearchView= (SearchView) inputView.getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_GO);
        mSearchView.setQueryHint(getString(R.string.web_url_input_hint));
        if (mUrl != null && mSearchView != null) mSearchView.setQuery(mUrl, true);
        mSearchView.onActionViewExpanded();
        MenuItemCompat.setOnActionExpandListener(inputView, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mSearchView.setQuery(mUrl, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
        getMenuInflater().inflate(R.menu.menu_web_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if (!TextUtils.isEmpty(mUrl)
                    && !DownloadUrlParser.parseGithubUrlAndDownload(SimpleWebActivity.this, mUrl)) {
                showMessage(getString(R.string.repo_download_url_parse_error));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebContent.canGoBack()) {
                        mWebContent.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!TextUtils.isEmpty(query)) {
            mUrl = query;
            loadUrl(mUrl);
            mSearchView.clearFocus();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            } else {
                if (mProgressBar.getVisibility() == View.GONE)
                    mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebContent.destroy();
    }
}