package com.loopeer.codereader.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopeer.codereader.R;

public class ProgressLoading extends Dialog {

    private ProgressBar mProgressBar;
    private TextView mMessageTextView;

    private Window mWindow;

    private CharSequence mMessage;
    private boolean mShowProgress = true;

    public ProgressLoading(Context context, int theme) {
        super(context, theme);
        initialize(context, theme);
    }

    private void initialize(Context context, int theme) {
        mWindow = getWindow();
        mWindow.requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.progress_loading);
        mMessageTextView = (TextView) findViewById(R.id.text_message);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
    }

    public ProgressLoading setMessage(int resId) {
        return setMessage(getContext().getString(resId));
    }

    public ProgressLoading setMessage(CharSequence msg) {
        mMessage = msg;
        return this;
    }

    public ProgressLoading updateMessage(CharSequence msg) {
        mMessage = msg;
        show();
        return this;
    }

    public ProgressLoading hideMessage() {
        return updateMessage("");
    }

    public ProgressLoading hideProgressBar() {
        mShowProgress = false;
        show();
        return this;
    }

    @Override
    public void show() {
        if (mMessageTextView != null) {
            if (TextUtils.isEmpty(mMessage)) {
                mMessageTextView.setVisibility(View.GONE);
            } else {
                mMessageTextView.setVisibility(View.VISIBLE);
                mMessageTextView.setText(mMessage);
            }
        }

        if (mProgressBar != null) {
            if (mShowProgress) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.GONE);
            }
        }
        super.show();
    }

}