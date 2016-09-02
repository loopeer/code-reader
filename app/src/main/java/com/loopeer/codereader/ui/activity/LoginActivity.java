package com.loopeer.codereader.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.loopeer.codereader.R;
import com.loopeer.codereader.api.ServiceFactory;
import com.loopeer.codereader.api.service.GithubService;
import com.loopeer.codereader.model.Empty;
import com.loopeer.codereader.model.Token;
import com.loopeer.codereader.ui.view.Checker;
import com.loopeer.codereader.ui.view.LoginChecker;
import com.loopeer.codereader.ui.view.TextWatcherImpl;
import com.loopeer.codereader.utils.Base64;
import com.loopeer.codereader.utils.SnackbarUtils;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseActivity implements Checker.CheckObserver {

    private static final String TAG = "LoginActivity";

    @BindView(R.id.edit_login_account)
    EditText mEditLoginAccount;
    @BindView(R.id.edit_login_password)
    EditText mEditLoginPassword;
    @BindView(R.id.btn_sign_in)
    Button mBtnSignIn;
    GithubService mGithubService;

    public final static String TOKEN_NOTE = "CodeReader APP Token";
    public final static String[] SCOPES = {"public_repo", "repo", "user", "gist"};
    private LoginChecker mLoginChecker;
    private String mBase64Str;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mGithubService = ServiceFactory.getGithubService();
        mLoginChecker = new LoginChecker(this);

        mEditLoginAccount.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                mLoginChecker.setUsername(editable.toString());
            }
        });
        mEditLoginPassword.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                mLoginChecker.setPassword(
                        editable.toString());
            }
        });
    }

    @OnClick(R.id.btn_sign_in)
    public void onClick() {
        final String username = mEditLoginAccount.getText().toString();
        final String password = mEditLoginPassword.getText().toString();
        mBase64Str = "Basic " + Base64.encode(username + ':' + password);
        createToken(mBase64Str);
    }

    private void createToken(String base64){

        final Token token = new Token();
        token.setNote(TOKEN_NOTE);
        token.setScopes(Arrays.asList(SCOPES));

        registerSubscription(
                mGithubService.createToken(token,base64)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(() -> showProgressLoading(""))
                        .observeOn(AndroidSchedulers.mainThread())
                        .doAfterTerminate(this::dismissProgressLoading)
                        .doOnNext(tokenResponse -> {
                            if(tokenResponse.isSuccessful()){
                                Log.d(TAG,tokenResponse.body().toString());
                                String t = tokenResponse.body().getToken();
                                SnackbarUtils.show(mBtnSignIn.getRootView(),t);
                            }else if(tokenResponse.code() == 401){
                                SnackbarUtils.show(mBtnSignIn.getRootView(),R.string.login_auth_error);
                            }else if(tokenResponse.code() == 403){
                                SnackbarUtils.show(mBtnSignIn.getRootView(),R.string.login_over_auth_error);
                            }else if(tokenResponse.code() == 422){
                                findCertainTokenID(base64);
                            }
                        })
                        .subscribe()
        );
    }

    private void findCertainTokenID(String base64){
        registerSubscription(
        mGithubService.listToken(base64)
                .flatMap(listResponse -> {
                    for(Token token : listResponse.body()){
                        if(TOKEN_NOTE.equals(token.getNote())){
                            return mGithubService.removeToken(base64,String.valueOf(token.getId()));
                        }
                    }
                    return Observable.empty();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(emptyResponse -> {
                    if(emptyResponse.code() == 204){
                        createToken(base64);
                    }
                })
                .subscribe()
        );
    }

    @Override
    public void check(boolean b) {
        mBtnSignIn.setEnabled(b);
    }
}
