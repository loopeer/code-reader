package com.loopeer.codereaderkt.ui.activity

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import com.loopeer.codereaderkt.Navigator
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.databinding.ActivityAboutBinding
import com.loopeer.codereaderkt.utils.CustomTextUtils
import com.loopeer.directorychooser.ColorClickableSpan


class AboutActivity: BaseActivity() {

    lateinit var binding : ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about)

        setUpVersion(this)
        setUpTextSpan()
    }

    private fun setUpTextSpan() {
        val aboutContent = resources.getString(R.string.about_content)
        val indexSource = CustomTextUtils.calculateTextStartEnd(aboutContent, resources.getString(R.string.about_coreader))
        val signPolicyTipSpan = SpannableString(aboutContent)
        signPolicyTipSpan.setSpan(object : ColorClickableSpan(this, R.color.colorPrimary) {
            override fun onClick(widget: View) {
                Navigator().startWebActivity(this@AboutActivity, getString(R.string.about_coreader_github_url))
            }
        }, indexSource[0], indexSource[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val indexEmail = CustomTextUtils.calculateTextStartEnd(aboutContent,
                resources.getString(R.string.about_email))
        signPolicyTipSpan.setSpan(object : ColorClickableSpan(this, R.color.colorPrimary) {
            override fun onClick(widget: View) {
                Navigator().startComposeEmail(this@AboutActivity,
                        arrayOf(getString(R.string.about_email)),
                        getString(R.string.app_name),
                        getString(R.string.about_email_content_tip))
            }
        }, indexEmail[0], indexEmail[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.textAboutContent.text = signPolicyTipSpan
        binding.textAboutContent.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setUpVersion(context: Context) {
        val packageManager = context.packageManager
        var packInfo: PackageInfo? = null
        try {
            packInfo = packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        var version = ""
        var code = ""
        if (packInfo != null) {
            version = packInfo.versionName
            code = Integer.valueOf(packInfo.versionCode)!!.toString()
        }

        binding.textAboutVersion.text = "V$version-$code"
    }
}