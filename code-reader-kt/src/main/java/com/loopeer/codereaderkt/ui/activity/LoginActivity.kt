package com.loopeer.codereaderkt.ui.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.Editable
import android.util.Log
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.api.ServiceFactory
import com.loopeer.codereaderkt.api.service.GithubService
import com.loopeer.codereaderkt.databinding.ActivityLoginBinding
import com.loopeer.codereaderkt.model.Empty
import com.loopeer.codereaderkt.model.Token
import com.loopeer.codereaderkt.ui.view.Checker
import com.loopeer.codereaderkt.ui.view.LoginChecker
import com.loopeer.codereaderkt.ui.view.TextWatcherImpl
import com.loopeer.codereaderkt.utils.Base64
import com.loopeer.codereaderkt.utils.SnackbarUtils
import retrofit2.Response
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*


class LoginActivity : BaseActivity(), Checker.CheckObserver {


    //登陆本身就有问题
    private val TAG = "LoginActivity"

    lateinit var binding: ActivityLoginBinding

    internal lateinit var mGithubService: GithubService

    val TOKEN_NOTE = "CodeReader APP Token"
    val SCOPES = arrayOf("public_repo", "repo", "user", "gist")

    private var mLoginChecker: LoginChecker? = null
    private var mBase64Str: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        mGithubService = ServiceFactory().getGithubService()
        mLoginChecker = LoginChecker(this)

        binding.editLoginAccount.addTextChangedListener(object : TextWatcherImpl() {
            override fun afterTextChanged(editable: Editable) {
                super.afterTextChanged(editable)
                mLoginChecker!!.username=editable.toString()
            }
        })
        binding.editLoginPassword.addTextChangedListener(object : TextWatcherImpl() {
            override fun afterTextChanged(editable: Editable) {
                super.afterTextChanged(editable)
                mLoginChecker!!.password = editable.toString()
            }
        })

    }

    private fun createToken(base64: String) {

        val token = Token()
        token.note = (TOKEN_NOTE)
        token.scopes = (Arrays.asList(*SCOPES))

        registerSubscription(
                mGithubService.createToken(token, base64)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe { showProgressLoading("") }
                        .observeOn(AndroidSchedulers.mainThread())
                        .doAfterTerminate({ this.dismissProgressLoading() })
                        .doOnNext { tokenResponse ->
                            when {
                                tokenResponse.isSuccessful -> {
                                    Log.d(TAG, tokenResponse.body().toString())
                                    val t = tokenResponse.body().token
                                    SnackbarUtils.show(binding.layoutContainer, t.toString())
                                }
                                tokenResponse.code() == 401 -> SnackbarUtils.show(binding.layoutContainer, R.string.login_auth_error)
                                tokenResponse.code() == 403 -> SnackbarUtils.show(binding.layoutContainer, R.string.login_over_auth_error)
                                tokenResponse.code() == 422 -> findCertainTokenID(base64)
                            }
                        }
                        .subscribe()
        )
    }

    private fun findCertainTokenID(base64: String) {
        registerSubscription(
                mGithubService.listToken(base64)
                        .flatMap<Response<Empty>> { listResponse ->
                            listResponse.body()
                                    .filter { TOKEN_NOTE == it.note }
                                    .forEach { return@flatMap mGithubService.removeToken(base64, it.id.toString()) }

                            Observable.empty<Response<Empty>>()
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { emptyResponse ->
                            if (emptyResponse.code() == 204) {
                                createToken(base64)
                            }
                        }
                        .subscribe()
        )
    }

    fun onSignInClick(){
        Log.d("LoginActivityLog","click")
        val username = binding.editLoginAccount.text.toString()
        val password = binding.editLoginPassword.text.toString()
        mBase64Str = "Basic " + Base64.encode(username + ':' + password)
        createToken(mBase64Str!!)
    }

    override fun check(b: Boolean) {
        //登陆按钮是否可点击
        binding.btnSignIn.isEnabled = b
    }

}