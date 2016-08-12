package com.loopeer.codereader.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BaseFragment extends Fragment {
    private final CompositeSubscription mAllSubscription = new CompositeSubscription();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }


    protected void registerSubscription(Subscription subscription) {
        mAllSubscription.add(subscription);
    }

    protected void unregisterSubscription(Subscription subscription) {
        mAllSubscription.remove(subscription);
    }

    protected void clearSubscription() {
        mAllSubscription.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearSubscription();
    }
}
