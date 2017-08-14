package com.loopeer.codereaderkt.ui.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.api.ServiceFactory
import com.loopeer.codereaderkt.api.service.GithubService
import com.loopeer.codereaderkt.databinding.ActivityLoginBinding


class LoginActivity : BaseActivity() {

    lateinit var binding: ActivityLoginBinding

    internal lateinit var mGithubService: GithubService

    val TOKEN_NOTE = "CodeReader APP Token"
    val SCOPES = arrayOf("public_repo", "repo", "user", "gist")

//    private var mLoginChecker: LoginChecker? = null
    private var mBase64Str: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        mGithubService = ServiceFactory.githubService

    }


}