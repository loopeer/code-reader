package com.loopeer.codereaderkt.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ViewAnimator
import com.loopeer.codereaderkt.Navigator
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.databinding.ActivityMainBinding
import com.loopeer.codereaderkt.model.Repo
import com.loopeer.codereaderkt.ui.adapter.ItemTouchHelperCallback
import com.loopeer.codereaderkt.ui.adapter.MainLatestAdapter
import com.loopeer.codereaderkt.ui.decoration.DividerItemDecoration
import com.loopeer.codereaderkt.ui.decoration.DividerItemDecorationMainList
import com.loopeer.codereaderkt.ui.loader.ILoadHelper
import com.loopeer.codereaderkt.ui.loader.RecyclerLoader
import com.loopeer.directorychooser.NavigatorChooser
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension


class MainActivity : BaseActivity() {

    private val TAG = "MainActivity"
    val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1000
    private lateinit var binding: ActivityMainBinding

//    private var mRecyclerLoader: ILoadHelper? = null
//    private var mMainLatestAdapter: MainLatestAdapter? = null

    lateinit var mItemTouchHelper: ItemTouchHelperExtension
    lateinit var mCallback: ItemTouchHelperExtension.Callback

//    internal var mRecyclerView: RecyclerView? = null
//    internal var mAnimatorRecyclerContent: ViewAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

//        mRecyclerView = findViewById(R.id.view_recycler) as RecyclerView
//        mAnimatorRecyclerContent = findViewById(R.id.view_coordinator_container) as ViewAnimator

        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_repo_add, menu)
        menuInflater.inflate(R.menu.menu_settings, menu)
        menuInflater.inflate(R.menu.menu_github, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> Navigator().startSettingActivity(this)
            R.id.action_repo_add -> Navigator().startAddRepoActivity(this)
            R.id.action_github -> Navigator().startLoginActivity(this)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
//        setUpView()
    }

    override fun onResume() {
        super.onResume()
//        mRecyclerLoader?.showProgress()
        loadLocalData()
    }

    private fun setUpView() {
//        mRecyclerLoader = RecyclerLoader(mAnimatorRecyclerContent!!)
//        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
//        mMainLatestAdapter = MainLatestAdapter(this)
//        mRecyclerView!!.adapter = mMainLatestAdapter
//        mRecyclerView!!.addItemDecoration(DividerItemDecorationMainList(this,
//                DividerItemDecoration.VERTICAL_LIST, resources.getDimensionPixelSize(R.dimen.repo_list_divider_start), -1, -1))
//        mItemTouchHelper = createItemTouchHelper()
//        mItemTouchHelper.attachToRecyclerView(mRecyclerView)
    }

    fun createItemTouchHelper(): ItemTouchHelperExtension {
        mCallback = createCallback()
        return ItemTouchHelperExtension(mCallback)
    }

    fun createCallback(): ItemTouchHelperExtension.Callback {
        return ItemTouchHelperCallback()
    }

    private fun loadLocalData() {
//        val repos = CoReaderDbHelper.getInstance(CodeReaderApplication.appContext!!).readRepos()
//        setUpContent(repos)
    }


    private fun setUpContent(repos: List<Repo>) {
//        mRecyclerLoader!!.showContent()
//        mMainLatestAdapter!!.updateData(repos)
    }




    override fun onPause() {
        super.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return
            }
        }
    }

    fun onFabClick(view: View) {
//        Toast.makeText(this, "Fab Clicked !", Toast.LENGTH_SHORT).show()
        doSelectFile()
    }

    fun doSelectFile() {
        NavigatorChooser.startDirectoryFileChooserActivity(this)
        //点击fab打开文件列表activity
    }
}