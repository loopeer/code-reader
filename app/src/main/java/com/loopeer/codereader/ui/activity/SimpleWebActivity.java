package com.loopeer.codereader.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.loopeer.codereader.Navigator;
import com.loopeer.codereader.R;
import com.loopeer.codereader.ui.view.NestedScrollWebView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleWebActivity extends BaseActivity {

    @BindView(R.id.web_content)
    NestedScrollWebView mWebContent;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.progress_bar_web)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_web);
        ButterKnife.bind(this);

        initWeb();
        parseIntent();
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
                view.loadUrl(url);
                return true;
            }

        });
        mWebContent.setWebChromeClient(new WebChromeClient());
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            } else {
                if (mProgressBar.getVisibility() == View.GONE) mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    private void parseIntent() {
        Intent intent = getIntent();
        String webUrl = intent.getStringExtra(Navigator.EXTRA_WEB_URL);
        String htmlString = intent.getStringExtra(Navigator.EXTRA_HTML_STRING);
        if (webUrl != null) loadUrl(webUrl);
        if (htmlString != null) loadData(htmlString);
    }

    private void loadData(String htmlString) {
        mWebContent.loadData(htmlString, "text/html", "utf-8");
    }

    private void loadUrl(String webUrl) {
        mWebContent.loadUrl(webUrl);
    }


}