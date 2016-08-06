package com.loopeer.codereader.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.loopeer.codereader.Navigator;
import com.loopeer.codereader.R;
import com.loopeer.codereader.model.Repo;
import com.loopeer.codereader.ui.adapter.MainLatestAdapter;
import com.loopeer.codereader.ui.decoration.DividerItemDecoration;
import com.loopeer.codereader.ui.loader.ILoadHelper;
import com.loopeer.codereader.ui.loader.RecyclerLoader;
import com.loopeer.codereader.utils.G;
import com.loopeer.codereader.utils.Settings;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1000;
    @BindView(R.id.view_recycler)
    RecyclerView mRecyclerContent;
    @BindView(R.id.animator_recycler_content)
    ViewAnimator mAnimatorRecyclerContent;
    @BindView(R.id.fab_main)
    FloatingActionButton mFabMain;

    private ILoadHelper mRecyclerLoader;
    private MainLatestAdapter mMainLatestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        G.settings = new Settings(this);
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
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setUpView();
    }

    private void setUpView() {
        mRecyclerLoader = new RecyclerLoader(mAnimatorRecyclerContent);
        mRecyclerContent.setLayoutManager(new LinearLayoutManager(this));
        mMainLatestAdapter = new MainLatestAdapter(this);
        mRecyclerContent.setAdapter(mMainLatestAdapter);
        mRecyclerContent.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST
                , getResources().getDimensionPixelSize(R.dimen.repo_list_divider_start)
                , -1
                , -1));
        mMainLatestAdapter.updateData(createTestData());
        mRecyclerLoader.showContent();
    }

    @OnClick(R.id.fab_main)
    @SuppressWarnings("unused")
    public void onClick() {
        doSelectFile();
    }

    private void doSelectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
                    Navigator.FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
    }


    /** 根据返回选择的文件，来进行上传操作 **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == Activity.RESULT_OK) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            Log.i("ht", "url" + uri);
/*
            String url;
            try {
//                url = FFileUtils.getPath(this, uri);
                Log.i("ht", "url" + uri);
//                String fileName = url.substring(url.lastIndexOf("/") + 1);

                *//*intent.putExtra("fileName", fileName);
                intent.putExtra("url", url);
                intent.putExtra("type ", "");
                intent.putExtra("fuid", "");
                intent.putExtra("type", "");*//*
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }*/
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private List<Repo> createTestData() {
        List<Repo> repos = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Repo repo = new Repo();
            repo.isFolder = true;
            repo.absolutePath = "asdgsdg";
            repos.add(repo);
        }
        for (int i = 0; i < 10; i++) {
            repos.add(new Repo());
        }
        return repos;
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
