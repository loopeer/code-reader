package com.loopeer.codereader.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;

import com.loopeer.codereader.Navigator;
import com.loopeer.codereader.R;
import com.loopeer.codereader.model.Repo;
import com.loopeer.codereader.ui.view.AddRepoChecker;
import com.loopeer.codereader.ui.view.Checker;
import com.loopeer.codereader.ui.view.TextWatcherImpl;
import com.loopeer.codereader.utils.DownloadUrlParser;
import com.loopeer.codereader.utils.FileCache;

import butterknife.BindView;
import butterknife.OnClick;

public class AddRepoActivity extends BaseActivity implements Checker.CheckObserver {

    @BindView(R.id.edit_add_repo_name)
    EditText mEditAddRepoName;
    @BindView(R.id.edit_add_repo_url)
    EditText mEditAddRepoUrl;
    @BindView(R.id.btn_add_repo)
    Button mBtnAddRepo;

    private AddRepoChecker mAddRepoChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_repo);

        mAddRepoChecker = new AddRepoChecker(this);
        mEditAddRepoName.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                mAddRepoChecker.setRepoName(editable.toString());
            }
        });
        mEditAddRepoUrl.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                mAddRepoChecker.setRepoDownloadUrl(editable.toString());
            }
        });
    }


    @Override
    public void check(boolean b) {
        mBtnAddRepo.setEnabled(b);
    }

    @OnClick(R.id.btn_add_repo)
    @SuppressWarnings("unused")
    public void onClick() {
        hideSoftInputMethod();
        if (!DownloadUrlParser.parseGithubUrlAndDownload(this, mAddRepoChecker.repoDownloadUrl.trim())) {
            showMessage(getString(R.string.repo_download_url_parse_error));
        } else {
            Repo repo = new Repo(
                    mAddRepoChecker.repoName.trim()
                    , FileCache.getInstance().getRepoAbsolutePath(mAddRepoChecker.repoName)
                    , mAddRepoChecker.repoDownloadUrl.trim()
                    , true
                    , 0);
            Navigator.startDownloadNewRepoService(this, repo);
            this.finish();
        }
    }
}

