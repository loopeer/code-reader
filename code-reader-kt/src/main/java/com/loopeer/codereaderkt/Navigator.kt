package com.loopeer.codereaderkt

import android.content.Context
import android.content.Intent
import com.loopeer.codereaderkt.ui.activity.AddRepoActivity
import com.loopeer.codereaderkt.ui.activity.LoginActivity
import com.loopeer.codereaderkt.ui.activity.MainActivity
import com.loopeer.codereaderkt.ui.activity.SettingActivity


class Navigator {
    val EXTRA_REPO = "extra_repo"
    val EXTRA_ID = "extra_id"
    val EXTRA_DOWNLOAD_SERVICE_TYPE = "extra_download_service_type"
    val EXTRA_DIRETORY_ROOT = "extra_diretory_root"
    val EXTRA_DIRETORY_ROOT_NODE_INSTANCE = "extra_diretory_root_node_instance"
    val EXTRA_DIRETORY_SELECTING = "extra_diretory_selecting"
    val EXTRA_WEB_URL = "extra_web_url"
    val EXTRA_HTML_STRING = "extra_html_string"

    fun startMainActivity(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        context.startActivity(intent)
    }

    fun startLoginActivity(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
    }

    fun startSettingActivity(context: Context) {
        val intent = Intent(context, SettingActivity::class.java)
        context.startActivity(intent)
    }

    fun startAddRepoActivity(context: Context) {
        val intent = Intent(context, AddRepoActivity::class.java)
        context.startActivity(intent)
    }

}