package com.loopeer.codereader.ui.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.loopeer.codereader.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

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

}
