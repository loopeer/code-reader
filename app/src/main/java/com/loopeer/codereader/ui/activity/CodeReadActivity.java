package com.loopeer.codereader.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.loopeer.codereader.R;
import com.loopeer.codereader.ui.view.DirectoryNavDelegate;

import butterknife.BindView;

public class CodeReadActivity extends BaseActivity {

    @BindView(R.id.directory_view)
    RecyclerView mDirectoryRecyclerView;
    @BindView(R.id.left_sheet)
    View mLeftSheet;

    private DirectoryNavDelegate mDirectoryNavDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_read);

        mDirectoryNavDelegate = new DirectoryNavDelegate(mDirectoryRecyclerView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.code_read, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
