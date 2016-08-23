package com.loopeer.codereader.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;

import com.loopeer.codereader.R;
import com.loopeer.codereader.utils.PrefUtils;
import com.loopeer.directorychooser.ForegroundLinearLayout;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.item_setting_font_size)
    ForegroundLinearLayout mItemSettingFontSize;
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
    }

    private void setUpView() {
        mCheckboxShowLineNumber.setOnCheckedChangeListener((compoundButton, b)
                -> PrefUtils.setPrefDisplayLineNumber(SettingActivity.this, b));
        mCheckboxMenloFont.setOnCheckedChangeListener((compoundButton, b)
                -> PrefUtils.setPrefMenlofont(SettingActivity.this, b));
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
}
