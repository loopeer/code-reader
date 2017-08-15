package com.loopeer.codereaderkt.ui.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.Editable
import com.loopeer.codereaderkt.Navigator
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.databinding.ActivityAddRepoBinding
import com.loopeer.codereaderkt.model.Repo
import com.loopeer.codereaderkt.ui.view.AddRepoChecker
import com.loopeer.codereaderkt.ui.view.Checker
import com.loopeer.codereaderkt.ui.view.TextWatcherImpl


class AddRepoActivity : BaseActivity(), Checker.CheckObserver {

    private var mAddRepoChecker: AddRepoChecker? = null
    private lateinit var binding: ActivityAddRepoBinding
    //需要重新rebuild一下才能出现对应的binding文件

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_repo)
        mAddRepoChecker = AddRepoChecker(this)


        binding.editAddRepoName.addTextChangedListener(object : TextWatcherImpl() {
            override fun afterTextChanged(editable: Editable) {
                super.afterTextChanged(editable)
                mAddRepoChecker!!.setRepoName(editable.toString())
            }
        })
        binding.editAddRepoUrl.addTextChangedListener(object : TextWatcherImpl() {
            override fun afterTextChanged(editable: Editable) {
                super.afterTextChanged(editable)
                mAddRepoChecker!!.setRepoDownloadUrl(editable.toString())
            }
        })
    }

    fun onDownClick() {
        hideSoftInputMethod()
/*开始下载并退回主界面
        val repo = Repo(
//                mAddRepoChecker!!.repoName.trim { it <= ' ' }, FileCache.getInstance().getRepoAbsolutePath(mAddRepoChecker.repoName), mAddRepoChecker.repoDownloadUrl.trim { it <= ' ' }, true, 0)
//        Navigator.startDownloadNewRepoService(this, repo)
        this.finish()
*/
    }

    override fun check(b: Boolean) {
        binding.btnAddRepo.isEnabled = b
    }

}