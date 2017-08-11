package com.loopeer.codereaderkt.ui.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.widget.SeekBar
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.databinding.ActivitySettingBinding


class SettingActivity : BaseActivity(), SeekBar.OnSeekBarChangeListener {

    lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}