package com.loopeer.codereader.ui.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.loopeer.codereader.R;
import com.loopeer.codereader.model.DirectoryNode;
import com.loopeer.codereader.utils.FileUtils;
import com.loopeer.codereader.utils.G;
import com.loopeer.codereader.utils.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.BindView;

public class CodeReadFragment extends BaseFragment {
    private static final String TAG = "CodeReadFragment";

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
        return inflater.inflate(R.layout.fragment_code_read, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebCodeRead.getSettings().setJavaScriptEnabled(true);
        mWebCodeRead.getSettings().setSupportZoom(true);
        mWebCodeRead.getSettings().setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT >= 11) {
            new Runnable() {
                @SuppressLint({"NewApi"})
                public void run() {
                    mWebCodeRead.getSettings().setDisplayZoomControls(false);
                }
            }.run();
        }
        openFile();
    }

    private void openFile() {
        if (FileUtils.isImageFileType(mNode.absolutePath)) {
            openImageFile();
        } else {
            openCodeFile();
        }
    }

    private void openImageFile() {
        String string = "<html>" +
                "<body style=\"margin-top: 40px; margin-bottom: 40px; text-align: center; vertical-align: center;\">"
                + "<img src='file:///"+ mNode.absolutePath +"'>"
                + "</body></html>";
         mWebCodeRead.loadDataWithBaseURL(null,string
                ,"text/html"
                ,"utf-8"
                , null);
    }

    public void openFile(DirectoryNode node) {
        mNode = node;
        openFile();
    }

    protected void openCodeFile() {
        InputStream stream = null;
        try {
            stream = new FileInputStream(mNode.absolutePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (stream == null)
            return;
        final InputStream finalStream = stream;
        new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String[] objects) {
                String[] names = mNode.name.split("\\.");
                String jsFile = G.fileExtToJSMap.getJsFileForExtension(names[names.length - 1]);
                if (jsFile == null) {
                    publishProgress(getActivity().getString(R.string.code_read_not_found_type));
                    return null;
                }
                StringBuilder sb = new StringBuilder();
                StringBuilder localStringBuilder = new StringBuilder();
                try {
                    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(finalStream, "UTF-8"));
                    for (; ; ) {
                        String str = localBufferedReader.readLine();
                        if (str == null) {
                            break;
                        }
                        localStringBuilder.append(str);
                        localStringBuilder.append("\n");
                    }

                    localBufferedReader.close();
                    sb.append("<pre class='brush: ");
                    sb.append(jsFile.toLowerCase());
                    sb.append(";'>");
                    sb.append(TextUtils.htmlEncode(localStringBuilder.toString()));
                    sb.append("</pre>");
                    return Utils.buildHtmlContent(getActivity(), sb.toString(), jsFile, mNode.name);
                } catch (OutOfMemoryError paramAnonymousVarArgs) {
                    Log.e(TAG, "Unable to open file, out of memory!");
                    return null;
                } catch (FileNotFoundException paramAnonymousVarArgs) {
                    return null;
                } catch (IOException paramAnonymousVarArgs) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(String o) {
                super.onPostExecute(o);
                if (o != null) {
                    mWebCodeRead.loadDataWithBaseURL("file:///android_asset/", o, "text/html", "UTF-8", "");
                    return;
                }
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                Toast.makeText(getContext(), values[0], Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mWebCodeRead.destroy();

    }
}
