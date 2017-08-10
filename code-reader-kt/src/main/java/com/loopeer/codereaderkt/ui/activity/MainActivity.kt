package com.loopeer.codereaderkt.ui.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.databinding.ActivityMainBinding

/**
 * Created by loopeer on 2017/8/10.
 */
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1000
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
    }
}