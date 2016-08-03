package com.loopeer.codereader.ui.activity;

import android.os.Bundle;
import com.loopeer.codereader.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Navigator.startCodeReadActivity(this);
    }
}
