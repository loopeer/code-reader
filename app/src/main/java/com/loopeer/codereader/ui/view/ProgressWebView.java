package com.loopeer.codereader.ui.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.loopeer.codereader.R;

public class ProgressWebView extends WebView {

  private ProgressBar mProgressBar;

  public ProgressWebView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
    mProgressBar.setIndeterminate(false);
    mProgressBar.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_horizontal_web_view));
    mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, android.R.drawable.progress_indeterminate_horizontal));
    mProgressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 5, 0, 0));
    mProgressBar.setMinimumHeight(16);
    addView(mProgressBar);
    setWebChromeClient(new WebChromeClient());
  }

  public void setProgressbarGone() {
    mProgressBar.setVisibility(GONE);
  }

  public class WebChromeClient extends android.webkit.WebChromeClient {
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
      if (newProgress == 100) {
        mProgressBar.setVisibility(GONE);
      } else {
        if (mProgressBar.getVisibility() == GONE) mProgressBar.setVisibility(VISIBLE);
        mProgressBar.setProgress(newProgress);
      }
      super.onProgressChanged(view, newProgress);
    }

  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    LayoutParams lp = (LayoutParams) mProgressBar.getLayoutParams();
    lp.x = l;
    lp.y = t;
    mProgressBar.setLayoutParams(lp);
    super.onScrollChanged(l, t, oldl, oldt);
  }
}
