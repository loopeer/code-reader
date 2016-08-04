package com.loopeer.codereader.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.loopeer.codereader.R;
import com.loopeer.codereader.model.DirectoryNode;

import butterknife.BindView;

public class CodeReadFragment extends BaseFragment {

    @BindView(R.id.web_code_read)
    WebView mWebCodeRead;

    private DirectoryNode mNode;

    public static CodeReadFragment newInstance(DirectoryNode node) {
        CodeReadFragment codeReadFragment = new CodeReadFragment();
        codeReadFragment.mNode = node;
        return codeReadFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_code_read, container, true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
