/*
package com.loopeer.codereaderkt.ui.activity

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.view.GravityCompat
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.loopeer.codereaderkt.Navigator
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.db.CoReaderDbHelper
import com.loopeer.codereaderkt.model.DirectoryNode
import com.loopeer.codereaderkt.model.Repo
import com.loopeer.codereaderkt.ui.fragment.CodeReadFragment
import com.loopeer.codereaderkt.ui.view.DirectoryNavDelegate
import com.loopeer.codereaderkt.ui.view.DrawerLayout
import com.loopeer.codereaderkt.utils.DeviceUtils


class CodeReadActivity : BaseActivity(), DirectoryNavDelegate.FileClickListener, DirectoryNavDelegate.LoadFileCallback {

//    @BindView(R.id.directory_view)
    internal var mDirectoryRecyclerView: RecyclerView? = null
//    @BindView(R.id.left_sheet)
    internal var mLeftSheet: View? = null
//    @BindView(R.id.container_code_read)
    internal var mContainer: FrameLayout? = null
//    @BindView(R.id.drawer_layout)
    internal var mDrawerLayout: DrawerLayout? = null

    private var mFragment: CodeReadFragment? = null
    private var mDirectoryNode: DirectoryNode? = null
    private var mSelectedNode: DirectoryNode? = null

    private var mDirectoryNavDelegate: DirectoryNavDelegate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_read)
        setupStatusBar()

        mDirectoryNavDelegate = DirectoryNavDelegate(mDirectoryRecyclerView, this)
        mDirectoryNavDelegate!!.setLoadFileCallback(this)
        createFragment(null)
        parseIntent(savedInstanceState)
    }

    private fun setupStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        mDirectoryRecyclerView!!.setPadding(0, DeviceUtils.statusBarHeight, 0, 0)
        mDirectoryRecyclerView!!.clipToPadding = true
    }

    private fun parseIntent(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            mDirectoryNode = savedInstanceState.getSerializable(Navigator.EXTRA_DIRETORY_ROOT) as DirectoryNode
            mSelectedNode = savedInstanceState.getSerializable(Navigator.EXTRA_DIRETORY_SELECTING) as DirectoryNode
            val rootNodeInstance = savedInstanceState.getSerializable(Navigator.EXTRA_DIRETORY_ROOT_NODE_INSTANCE) as DirectoryNode
            mFragment!!.updateRootNode(mDirectoryNode!!)
            mDirectoryNavDelegate!!.resumeDirectoryState(rootNodeInstance)
            doOpenFile(mSelectedNode)
            return
        }
        val intent = intent
        val repo = intent.getSerializableExtra(Navigator.EXTRA_REPO) as Repo
        CoReaderDbHelper.getInstance(this).updateRepoLastModify(java.lang.Long.valueOf(repo.id)!!, System.currentTimeMillis())
        mDirectoryNode = repo.toDirectoryNode()
        mFragment!!.updateRootNode(mDirectoryNode!!)
        mDirectoryNavDelegate!!.updateData(mDirectoryNode)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(Navigator.EXTRA_DIRETORY_ROOT, mDirectoryNode)
        outState.putSerializable(Navigator.EXTRA_DIRETORY_SELECTING, mSelectedNode)
        outState.putSerializable(Navigator.EXTRA_DIRETORY_ROOT_NODE_INSTANCE, mDirectoryNavDelegate!!.getDirectoryNodeInstance())
    }

    override fun onBackPressed() {
        if (mDrawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_code_read_go_out, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_go_out) {
            finish()
            return true
        }
        if (id == android.R.id.home) {
            if (!mDrawerLayout!!.isDrawerOpen(GravityCompat.START))
                mDrawerLayout!!.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDirectoryNavDelegate!!.clearSubscription()
        mDirectoryNavDelegate = null
    }

    override fun doOpenFile(node: DirectoryNode?) {
        setTitle(if (node == null) mDirectoryNode!!.name else node!!.name)
        mSelectedNode = node
        loadCodeData(node)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun loadCodeData(node: DirectoryNode?) {
        mDrawerLayout!!.closeDrawer(GravityCompat.START)
        if (node != null) {
            mFragment!!.openFile(node)
        }
    }

    private fun createFragment(node: DirectoryNode?) {
        mFragment = CodeReadFragment.newInstance(node, mDirectoryNode)
        mFragment!!.setArguments(intent.extras)
        supportFragmentManager.beginTransaction()
                .add(R.id.container_code_read, mFragment).commit()
    }

    override fun onFileOpenStart() {
        if (mFragment != null && mFragment!!.isVisible())
            mFragment!!.getCodeContentLoader().showProgress()
    }

    override fun onFileOpenEnd() {

    }

}*/
