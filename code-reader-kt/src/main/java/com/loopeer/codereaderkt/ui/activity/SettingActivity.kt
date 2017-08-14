package com.loopeer.codereaderkt.ui.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.widget.SeekBar
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.databinding.ActivitySettingBinding
import com.loopeer.codereaderkt.event.ThemeRecreateEvent
import com.loopeer.codereaderkt.ui.view.ThemeChooser
import com.loopeer.codereaderkt.utils.PrefUtils
import com.loopeer.codereaderkt.utils.RxBus
import com.loopeer.codereaderkt.utils.ThemeUtils


class SettingActivity : BaseActivity(), SeekBar.OnSeekBarChangeListener , ThemeChooser.OnItemSelectListener{


    lateinit var binding: ActivitySettingBinding
    private var mThemeChooser: ThemeChooser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)

        mThemeChooser = ThemeChooser(this, this)
        mThemeChooser!!.addItem(binding.viewSettingThemeDay.id, ThemeUtils.THEME_DAY)
        mThemeChooser!!.addItem(binding.viewSettingThemeNight.id, ThemeUtils.THEME_NIGHT)
        initViewData()
        setUpView()
    }

    private fun initViewData() {
        binding.checkboxShowLineNumber.isChecked = PrefUtils.getPrefDisplayLineNumber(this)
        binding.checkboxMenloFont.isChecked = PrefUtils.getPrefMenlofont(this)
        val fontSize = PrefUtils.getPrefFontSize(this).toInt()
        binding.seekbarSettingFontSize.progress = fontSize
        binding.textSettingFontCurrent.text = fontSize.toString()
        binding.textSettingFontSizeTemp.textSize = fontSize.toFloat()
        mThemeChooser!!.onItemSelectByTag(PrefUtils.getPrefTheme(this))
    }

    private fun setUpView() {
        binding.checkboxShowLineNumber.setOnCheckedChangeListener({ _, b -> PrefUtils.setPrefDisplayLineNumber(this@SettingActivity, b) })
        binding.checkboxMenloFont.setOnCheckedChangeListener({ _, b -> PrefUtils.setPrefMenlofont(this@SettingActivity, b) })
        binding.seekbarSettingFontSize.setOnSeekBarChangeListener(this)
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        binding.textSettingFontSizeTemp.textSize = i.toFloat()
        PrefUtils.setPrefFontSize(this, i.toFloat())
        binding.textSettingFontCurrent.text = i.toString()
    }



    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }

    override fun onItemSelect(id: Int, tag: String) {
        if (PrefUtils.getPrefTheme(this) == tag) {
            return
        }
        AppCompatDelegate.setDefaultNightMode(if (tag == ThemeUtils.THEME_DAY)
            AppCompatDelegate.MODE_NIGHT_NO
        else
            AppCompatDelegate.MODE_NIGHT_YES)

        RxBus.getInstance().send(ThemeRecreateEvent())
        PrefUtils.setPrefTheme(this, tag)
    }
}