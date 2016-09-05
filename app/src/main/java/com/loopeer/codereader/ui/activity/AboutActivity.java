package com.loopeer.codereader.ui.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.loopeer.codereader.Navigator;
import com.loopeer.codereader.R;
import com.loopeer.codereader.utils.CustomTextUtils;
import com.loopeer.directorychooser.ColorClickableSpan;

import butterknife.BindView;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.text_about_version)
    TextView mTextAboutVersion;
    @BindView(R.id.text_about_content)
    TextView mTextAboutContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setUpVersion(this);
        setUpTextSpan();
    }

    private void setUpTextSpan() {
        String aboutContent = getResources().getString(R.string.about_content);
        int[] indexSource = CustomTextUtils.calculateTextStartEnd(aboutContent, getResources().getString(R.string.about_coreader));
        SpannableString aboutContentSpan = new SpannableString(aboutContent);
        aboutContentSpan.setSpan(new ColorClickableSpan(this, R.color.colorPrimary) {
            @Override
            public void onClick(View widget) {
                Navigator.startWebActivity(AboutActivity.this, getString(R.string.about_coreader_github_url));
            }
        }, indexSource[0], indexSource[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int[] indexEmail = CustomTextUtils.calculateTextStartEnd(aboutContent,
                getResources().getString(R.string.about_email));
        aboutContentSpan.setSpan(new ColorClickableSpan(this, R.color.colorPrimary) {
            @Override
            public void onClick(View widget) {
                Navigator.startComposeEmail(AboutActivity.this,
                        new String[]{getString(R.string.about_email)},
                        getString(R.string.app_name),
                        getString(R.string.about_email_content_tip));
            }
        }, indexEmail[0], indexEmail[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int[] indexFirDownload = CustomTextUtils.calculateTextStartEnd(aboutContent,
                getResources().getString(R.string.about_fir_download));
        aboutContentSpan.setSpan(new ColorClickableSpan(this, R.color.colorPrimary) {
            @Override
            public void onClick(View widget) {
                Navigator.startOutWebActivity(AboutActivity.this, getString(R.string.about_fir_download_url));
            }
        }, indexFirDownload[0], indexFirDownload[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextAboutContent.setText(aboutContentSpan);
        mTextAboutContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setUpVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String version = "";
        String code = "";
        if (packInfo != null) {
            version = packInfo.versionName;
            code = Integer.valueOf(packInfo.versionCode).toString();
        }

        mTextAboutVersion.setText("V" + version + "-" + code);
    }
}
