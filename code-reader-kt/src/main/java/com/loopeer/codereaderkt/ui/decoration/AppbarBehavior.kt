package com.loopeer.codereaderkt.ui.decoration

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View


class AppbarBehavior : AppBarLayout.Behavior {

    constructor() {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onStartNestedScroll(parent: CoordinatorLayout?, child: AppBarLayout?, directTargetChild: View?, target: View?, nestedScrollAxes: Int): Boolean {
        var result = super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes)
        if (result && !target!!.canScrollVertically(1) && child!!.y >= 0)
            result = false
        return result
    }
}