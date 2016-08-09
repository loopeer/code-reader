package com.loopeer.codereader.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopeer.codereader.R;
import com.loopeer.codereader.ui.view.ProgressWebView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleWebActivity extends BaseActivity {

    @BindView(R.id.web_content)
    ProgressWebView mWebContent;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

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
    }

    private void parseIntent() {
/*
        Intent intent = getIntent();
        String webUrl = intent.getStringExtra(Navigator.EXTRA_WEB_URL);
        String htmlString = intent.getStringExtra(Navigator.EXTRA_HTML_STRING);
        if (webUrl != null) loadUrl(webUrl);
        if (htmlString != null) loadData(htmlString);
*/
    }

    private void loadData(String htmlString) {
        mWebContent.loadData(htmlString, "text/html", "utf-8");
    }

    private void loadUrl(String webUrl) {
        mWebContent.loadUrl(webUrl);
    }


}