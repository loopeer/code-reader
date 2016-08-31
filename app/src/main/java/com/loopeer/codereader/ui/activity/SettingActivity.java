package com.loopeer.codereader.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.loopeer.codereader.Navigator;
import com.loopeer.codereader.R;
import com.loopeer.codereader.event.ThemeRecreateEvent;
import com.loopeer.codereader.ui.view.ForegroundRelativeLayout;
import com.loopeer.codereader.ui.view.ThemeChooser;
import com.loopeer.codereader.utils.PrefUtils;
import com.loopeer.codereader.utils.RxBus;
import com.loopeer.codereader.utils.ThemeUtils;
import com.loopeer.directorychooser.ForegroundLinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener, ThemeChooser.OnItemSelectListener {



    @BindView(R.id.item_setting_font_size)
    ForegroundRelativeLayout mItemSettingFontSize;
    @BindView(R.id.item_setting_line_number)
    ForegroundLinearLayout mItemSettingLineNumber;
    @BindView(R.id.item_setting_use_menlo)
    ForegroundLinearLayout mItemSettingUseMenlo;
    @BindView(R.id.item_setting_theme)
    ForegroundLinearLayout mItemSettingTheme;
    @BindView(R.id.checkbox_show_line_number)
    AppCompatCheckBox mCheckboxShowLineNumber;
    @BindView(R.id.checkbox_menlo_font)
    AppCompatCheckBox mCheckboxMenloFont;
    @BindView(R.id.text_setting_font_size_temp)
    TextView mTextSettingFontSizeTemp;
    @BindView(R.id.seekbar_setting_font_size)
    AppCompatSeekBar mSeekbarSettingFontSize;
    @BindView(R.id.text_setting_font_current)
    TextView mTextSettingFontCurrent;
    @BindView(R.id.view_setting_theme_day)
    ImageView mViewSettingThemeDay;
    @BindView(R.id.view_setting_theme_night)
    ImageView mViewSettingThemeNight;

    private ThemeChooser mThemeChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        mThemeChooser = new ThemeChooser(this, this);
        mThemeChooser.addItem(mViewSettingThemeDay.getId(), ThemeUtils.THEME_DAY);
        mThemeChooser.addItem(mViewSettingThemeNight.getId(), ThemeUtils.THEME_NIGHT);
        initViewData();
        setUpView();
    }

    private void initViewData() {
        mCheckboxShowLineNumber.setChecked(PrefUtils.getPrefDisplayLineNumber(this));
        mCheckboxMenloFont.setChecked(PrefUtils.getPrefMenlofont(this));
        int fontSize = (int) PrefUtils.getPrefFontSize(this);
        mSeekbarSettingFontSize.setProgress(fontSize);
        mTextSettingFontCurrent.setText(String.valueOf(fontSize));
        mTextSettingFontSizeTemp.setTextSize(fontSize);
        mThemeChooser.onItemSelectByTag(PrefUtils.getPrefTheme(this));
    }

    private void setUpView() {
        mCheckboxShowLineNumber.setOnCheckedChangeListener((compoundButton, b)
                -> PrefUtils.setPrefDisplayLineNumber(SettingActivity.this, b));
        mCheckboxMenloFont.setOnCheckedChangeListener((compoundButton, b)
                -> PrefUtils.setPrefMenlofont(SettingActivity.this, b));
        mSeekbarSettingFontSize.setOnSeekBarChangeListener(this);
    }

    @OnClick({
            R.id.item_setting_font_size,
            R.id.item_setting_line_number,
            R.id.item_setting_use_menlo,
            R.id.item_setting_theme,
            R.id.view_setting_theme_day,
            R.id.view_setting_theme_night,
            R.id.item_setting_about
    })
    @SuppressWarnings("unused")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_setting_font_size:

                break;
            case R.id.item_setting_line_number:
                mCheckboxShowLineNumber.setChecked(!mCheckboxShowLineNumber.isChecked());
                break;
            case R.id.item_setting_use_menlo:
                mCheckboxMenloFont.setChecked(!mCheckboxMenloFont.isChecked());
                break;
            case R.id.item_setting_theme:

                break;
            case R.id.view_setting_theme_day:
            case R.id.view_setting_theme_night:
                mThemeChooser.onItemSelect(view);
                break;
            case R.id.item_setting_about:
                Navigator.startAboutActivity(this);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        mTextSettingFontSizeTemp.setTextSize(i);
        PrefUtils.setPrefFontSize(this, i);
        mTextSettingFontCurrent.setText(String.valueOf(i));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onItemSelect(int id, String tag) {
        AppCompatDelegate.setDefaultNightMode(tag.equals(ThemeUtils.THEME_DAY)
                ? AppCompatDelegate.MODE_NIGHT_NO
                : AppCompatDelegate.MODE_NIGHT_YES);

        RxBus.getInstance().send(new ThemeRecreateEvent());
        PrefUtils.setPrefTheme(this, tag);
    }
}
