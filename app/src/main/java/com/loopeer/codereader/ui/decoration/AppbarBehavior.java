package com.loopeer.codereader.ui.decoration;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

public class AppbarBehavior extends AppBarLayout.Behavior {

    public AppbarBehavior() {
    }

    public AppbarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        boolean result = super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
        if (result && !target.canScrollVertically(1))
            result = false;
        return result;
    }
}
