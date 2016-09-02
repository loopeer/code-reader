package com.loopeer.codereader.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ViewAnimator;

import com.loopeer.codereader.CodeReaderApplication;
import com.loopeer.codereader.Navigator;
import com.loopeer.codereader.R;
import com.loopeer.codereader.coreader.db.CoReaderDbHelper;
import com.loopeer.codereader.event.DownloadFailDeleteEvent;
import com.loopeer.codereader.model.Repo;
import com.loopeer.codereader.sync.DownloadRepoService;
import com.loopeer.codereader.ui.adapter.ItemTouchHelperCallback;
import com.loopeer.codereader.ui.adapter.MainLatestAdapter;
import com.loopeer.codereader.ui.decoration.DividerItemDecoration;
import com.loopeer.codereader.ui.decoration.DividerItemDecorationMainList;
import com.loopeer.codereader.ui.loader.ILoadHelper;
import com.loopeer.codereader.ui.loader.RecyclerLoader;
import com.loopeer.codereader.utils.RxBus;
import com.loopeer.directorychooser.FileNod;
import com.loopeer.directorychooser.NavigatorChooser;
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1000;
    @BindView(R.id.view_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.animator_recycler_content)
    ViewAnimator mAnimatorRecyclerContent;
    @BindView(R.id.fab_main)
    FloatingActionButton mFabMain;

    private ILoadHelper mRecyclerLoader;
    private MainLatestAdapter mMainLatestAdapter;

    public ItemTouchHelperExtension mItemTouchHelper;
    public ItemTouchHelperExtension.Callback mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Navigator.startDownloadRepoService(this, DownloadRepoService.DOWNLOAD_PROGRESS);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_repo_add, menu);
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        getMenuInflater().inflate(R.menu.menu_github,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Navigator.startSettingActivity(this);
                break;
            case R.id.action_repo_add:
                Navigator.startAddRepoActivity(this);
                break;
            case R.id.action_github:
                Navigator.startLoginActivity(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setUpView();
        registerSubscription(
                RxBus.getInstance().toObservable()
                        .filter(o -> o instanceof DownloadFailDeleteEvent)
                        .map(o -> ((DownloadFailDeleteEvent) o).deleteRepo)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(mMainLatestAdapter::deleteRepo)
                        .subscribe()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecyclerLoader.showProgress();
        loadLocalData();
    }

    private void setUpView() {
        mRecyclerLoader = new RecyclerLoader(mAnimatorRecyclerContent);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMainLatestAdapter = new MainLatestAdapter(this);
        mRecyclerView.setAdapter(mMainLatestAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecorationMainList(this,
                DividerItemDecoration.VERTICAL_LIST
                , getResources().getDimensionPixelSize(R.dimen.repo_list_divider_start)
                , -1
                , -1));
        mItemTouchHelper = createItemTouchHelper();
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    public ItemTouchHelperExtension createItemTouchHelper() {
        mCallback = createCallback();
        return new ItemTouchHelperExtension(mCallback);
    }

    public ItemTouchHelperExtension.Callback createCallback() {
        return new ItemTouchHelperCallback();
    }

    private void loadLocalData() {
        List<Repo> repos =
                CoReaderDbHelper.getInstance(CodeReaderApplication.getAppContext()).readRepos();
        setUpContent(repos);
    }

    @Override
    protected void reCreateRefresh() {
        super.reCreateRefresh();
        mRecyclerView.getRecycledViewPool().clear();
        mMainLatestAdapter.notifyDataSetChanged();
    }

    private void setUpContent(List<Repo> repos) {
        mRecyclerLoader.showContent();
        mMainLatestAdapter.updateData(repos);
    }

    @OnClick(R.id.fab_main)
    @SuppressWarnings("unused")
    public void onClick() {
        doSelectFile();
    }

    private void doSelectFile() {
        NavigatorChooser.startDirectoryFileChooserActivity(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case NavigatorChooser.DIRECTORY_FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    FileNod node = (FileNod) data.getSerializableExtra(NavigatorChooser.EXTRA_FILE_NODE);
                    Repo repo = Repo.parse(node);
                    repo.id = String.valueOf(CoReaderDbHelper.getInstance(this).insertRepo(repo));
                    Navigator.startCodeReadActivity(MainActivity.this, repo);
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMainLatestAdapter.clearSubscription();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }

}
