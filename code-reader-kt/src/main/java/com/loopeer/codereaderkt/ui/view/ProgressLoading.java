package com.loopeer.codereaderkt.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.loopeer.codereaderkt.R;

public class ProgressLoading extends Dialog {

    private View mProgressView;
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
        mWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.progress_loading);
        mMessageTextView = (TextView) findViewById(R.id.text_message);
        mProgressView = findViewById(R.id.progress);
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

        if (mProgressView != null) {
            if (mShowProgress) {
                mProgressView.setVisibility(View.VISIBLE);
            } else {
                mProgressView.setVisibility(View.GONE);
            }
        }
        super.show();
    }

}