package com.loopeer.codereader.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.loopeer.codereader.Navigator;
import com.loopeer.codereader.R;
import com.loopeer.codereader.coreader.db.CoReaderDbHelper;
import com.loopeer.codereader.model.DirectoryNode;
import com.loopeer.codereader.model.Repo;
import com.loopeer.codereader.ui.fragment.CodeReadFragment;
import com.loopeer.codereader.ui.view.DirectoryNavDelegate;
import com.loopeer.codereader.ui.view.DrawerLayout;

import butterknife.BindView;

public class CodeReadActivity extends BaseActivity implements DirectoryNavDelegate.FileClickListener {

    @BindView(R.id.directory_view)
    RecyclerView mDirectoryRecyclerView;
    @BindView(R.id.left_sheet)
    View mLeftSheet;
    @BindView(R.id.container_code_read)
    FrameLayout mContainer;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private CodeReadFragment mFragment;
    private DirectoryNode mDirectoryNode;
    private DirectoryNode mSelectedNode;

    private DirectoryNavDelegate mDirectoryNavDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_read);

        mDirectoryNavDelegate = new DirectoryNavDelegate(mDirectoryRecyclerView, this);
        parseIntent(savedInstanceState);

    }

    private void parseIntent(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mDirectoryNode = (DirectoryNode) savedInstanceState.getSerializable(Navigator.EXTRA_DIRETORY_ROOT);
            mSelectedNode = (DirectoryNode) savedInstanceState.getSerializable(Navigator.EXTRA_DIRETORY_SELECTING);
            mDirectoryNavDelegate.updateData(mDirectoryNode, mSelectedNode);
            doOpenFile(mSelectedNode);
            return;
        }
        Intent intent = getIntent();
        Repo repo = (Repo) intent.getSerializableExtra(Navigator.EXTRA_REPO);
        CoReaderDbHelper.getInstance(this).updateRepoLastModify(Long.valueOf(repo.id)
                , System.currentTimeMillis());
        mDirectoryNode = repo.toDirectoryNode();
        mDirectoryNavDelegate.updateData(mDirectoryNode, null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Navigator.EXTRA_DIRETORY_ROOT, mDirectoryNode);
        outState.putSerializable(Navigator.EXTRA_DIRETORY_SELECTING, mSelectedNode);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_code_read_go_out, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_go_out) {
            finish();
            return true;
        }
        if (id == android.R.id.home) {
            if (!mDrawerLayout.isDrawerOpen(GravityCompat.START))
                mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDirectoryNavDelegate.clearSubscription();
    }

    @Override
    public void doOpenFile(DirectoryNode node) {
        setTitle(node == null ? mDirectoryNode.name : node.name);
        mSelectedNode = node;
        loadCodeData(node);
    }

    private void loadCodeData(DirectoryNode node) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if (mFragment == null) {
            mFragment = CodeReadFragment.newInstance(node);
            mFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_code_read, mFragment).commit();
        } else {
            mFragment.openFile(node);
        }
    }
}
