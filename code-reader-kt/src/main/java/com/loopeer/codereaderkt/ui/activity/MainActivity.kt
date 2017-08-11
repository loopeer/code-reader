package com.loopeer.codereaderkt.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.loopeer.codereaderkt.Navigator
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.databinding.ActivityMainBinding
import com.loopeer.codereaderkt.ui.adapter.MainLatestAdapter
import com.loopeer.directorychooser.NavigatorChooser


class MainActivity : BaseActivity() {

    private val TAG = "MainActivity"
    val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1000
    private lateinit var binding: ActivityMainBinding
    private var mMainLatestAdapter: MainLatestAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
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
//            R.id.action_settings -> Toast.makeText(this, "action_settings", Toast.LENGTH_SHORT).show()
            R.id.action_repo_add -> Navigator().startAddRepoActivity(this)
//            R.id.action_repo_add -> Toast.makeText(this, "action_repo_add", Toast.LENGTH_SHORT).show()
//            R.id.action_github -> Navigator.startLoginActivity(this)
            R.id.action_github -> Toast.makeText(this, "action_github", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
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
        doSelectFile();
    }

    fun doSelectFile() {
        NavigatorChooser.startDirectoryFileChooserActivity(this)
        //点击fab打开文件列表activity
    }
}