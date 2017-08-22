package com.loopeer.codereaderkt.ui.activity

import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.loopeer.codereaderkt.Navigator
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.databinding.ActivityCodeReadBinding
import com.loopeer.codereaderkt.db.CoReaderDbHelper
import com.loopeer.codereaderkt.model.DirectoryNode
import com.loopeer.codereaderkt.model.Repo
import com.loopeer.codereaderkt.ui.fragment.CodeReadFragment
import com.loopeer.codereaderkt.ui.view.DirectoryNavDelegate
import com.loopeer.codereaderkt.utils.DeviceUtils


class CodeReadActivity : BaseActivity(), DirectoryNavDelegate.FileClickListener, DirectoryNavDelegate.LoadFileCallback {

    internal var mLeftSheet: View? = null
    internal var mContainer: FrameLayout? = null

    private lateinit var mFragment: CodeReadFragment
    private lateinit var mDirectoryNode: DirectoryNode
    private lateinit var mSelectedNode: DirectoryNode

    private lateinit var mDirectoryNavDelegate: DirectoryNavDelegate

    lateinit var binding: ActivityCodeReadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_code_read)
        setupStatusBar()

        mDirectoryNavDelegate = DirectoryNavDelegate(binding.directoryView, this)
        mDirectoryNavDelegate.setLoadFileCallback(this)
        createFragment(null)
        parseIntent(savedInstanceState)
    }

    private fun setupStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        binding.directoryView.setPadding(0, DeviceUtils.statusBarHeight, 0, 0)
        binding.directoryView.clipToPadding = true
    }

    private fun parseIntent(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            mDirectoryNode = savedInstanceState.getSerializable(Navigator.EXTRA_DIRETORY_ROOT) as DirectoryNode
            mSelectedNode = savedInstanceState.getSerializable(Navigator.EXTRA_DIRETORY_SELECTING) as DirectoryNode
            val rootNodeInstance = savedInstanceState.getSerializable(Navigator.EXTRA_DIRETORY_ROOT_NODE_INSTANCE) as DirectoryNode
            mFragment.updateRootNode(mDirectoryNode)
            mDirectoryNavDelegate.resumeDirectoryState(rootNodeInstance)
            doOpenFile(mSelectedNode)
            return
        }
        val intent = intent
        val repo = intent.getSerializableExtra(Navigator.EXTRA_REPO) as Repo
        CoReaderDbHelper.getInstance(this).updateRepoLastModify(java.lang.Long.valueOf(repo.id),
                System.currentTimeMillis())
        mDirectoryNode = repo.toDirectoryNode()
        mFragment.updateRootNode(mDirectoryNode)
        mDirectoryNavDelegate.updateData(mDirectoryNode)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(Navigator.EXTRA_DIRETORY_ROOT, mDirectoryNode)
        outState.putSerializable(Navigator.EXTRA_DIRETORY_SELECTING, mSelectedNode)
        outState.putSerializable(Navigator.EXTRA_DIRETORY_ROOT_NODE_INSTANCE, mDirectoryNavDelegate.getDirectoryNodeInstance())
    }

    override fun onBackPressed() {
        if (binding.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout!!.closeDrawer(GravityCompat.START)
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
            if (!binding.drawerLayout!!.isDrawerOpen(GravityCompat.START))
                binding.drawerLayout!!.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        mDirectoryNavDelegate.clearSubscription()
        mDirectoryNavDelegate = null!!
    }

    override fun doOpenFile(node: DirectoryNode?) {
        title = if (node == null) mDirectoryNode.name else node.name
        mSelectedNode = node!!
        loadCodeData(node)
    }

    private fun loadCodeData(node: DirectoryNode?) {
        binding.drawerLayout!!.closeDrawer(GravityCompat.START)
        if (node != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mFragment.openFile(node)
            }
        }
    }

    private fun createFragment(node: DirectoryNode?) {
        mFragment = CodeReadFragment().newInstance(node!!, mDirectoryNode)
        mFragment.arguments = intent.extras
        supportFragmentManager.beginTransaction()
                .add(R.id.container_code_read, mFragment).commit()
    }

    override fun onFileOpenStart() {
        if (mFragment.isVisible)
            mFragment.getCodeContentLoader()!!.showProgress()
    }

    override fun onFileOpenEnd() {

    }

}
