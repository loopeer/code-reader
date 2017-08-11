package com.loopeer.codereaderkt.ui.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.databinding.ActivityAddRepoBinding
import com.loopeer.codereaderkt.ui.view.AddRepoChecker
import com.loopeer.codereaderkt.ui.view.Checker


class AddRepoActivity : BaseActivity(), Checker.CheckObserver {

    private var mAddRepoChecker: AddRepoChecker? = null
    private lateinit var binding: ActivityAddRepoBinding
    //需要重新rebuild一下才能出现对应的binding文件

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_repo)
        mAddRepoChecker = AddRepoChecker(this)
    }

    override fun check(b: Boolean) {

    }

}