package com.loopeer.codereader.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.loopeer.codereader.R;
import com.loopeer.codereader.ui.view.ForegroundRelativeLayout;
import com.loopeer.codereader.utils.PrefUtils;
import com.loopeer.directorychooser.ForegroundLinearLayout;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

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
            R.id.item_setting_theme
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
}
