package com.loopeer.codereader.ui.loader;

import android.view.View;
import android.widget.ViewAnimator;

import com.loopeer.codereader.R;
import com.loopeer.codereader.ui.view.ProgressIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CodeFragmentContentLoader implements ILoadHelper {

    @BindView(R.id.progress_code_fragment)
    ProgressIndicatorView mProgressIndicatorView;
    @BindView(R.id.content_animator)
    ViewAnimator mContentAnimator;

    public CodeFragmentContentLoader(View contentView) {
        ButterKnife.bind(this, contentView);
    }

    @Override
    public void showProgress() {
        mContentAnimator.setDisplayedChild(1);
        mProgressIndicatorView.setAnimationStatus(ProgressIndicatorView.AnimStatus.START);
    }

    @Override
    public void showContent() {
        mContentAnimator.setDisplayedChild(0);
        mProgressIndicatorView.setAnimationStatus(ProgressIndicatorView.AnimStatus.CANCEL);
    }

    @Override
    public void showEmpty() {
        mContentAnimator.setDisplayedChild(2);
        mProgressIndicatorView.setAnimationStatus(ProgressIndicatorView.AnimStatus.CANCEL);
    }
}
