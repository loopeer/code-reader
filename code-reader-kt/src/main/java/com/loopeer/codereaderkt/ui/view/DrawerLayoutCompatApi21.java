
package com.loopeer.codereaderkt.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

class DrawerLayoutCompatApi21 {

    private static final int[] THEME_ATTRS = {
            android.R.attr.colorPrimaryDark
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public static void configureApplyInsets(View drawerLayout) {
        if (drawerLayout instanceof DrawerLayoutImpl) {
            drawerLayout.setOnApplyWindowInsetsListener(new InsetsListener());
            drawerLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public static void dispatchChildInsets(View child, Object insets, int gravity) {
        WindowInsets wi = (WindowInsets) insets;
        if (gravity == Gravity.LEFT) {
            wi = wi.replaceSystemWindowInsets(wi.getSystemWindowInsetLeft(),
                    wi.getSystemWindowInsetTop(), 0, wi.getSystemWindowInsetBottom());
        } else if (gravity == Gravity.RIGHT) {
            wi = wi.replaceSystemWindowInsets(0, wi.getSystemWindowInsetTop(),
                    wi.getSystemWindowInsetRight(), wi.getSystemWindowInsetBottom());
        }
        child.dispatchApplyWindowInsets(wi);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public static void applyMarginInsets(ViewGroup.MarginLayoutParams lp, Object insets,
                                         int gravity) {
        WindowInsets wi = (WindowInsets) insets;
        if (gravity == Gravity.LEFT) {
            wi = wi.replaceSystemWindowInsets(wi.getSystemWindowInsetLeft(),
                    wi.getSystemWindowInsetTop(), 0, wi.getSystemWindowInsetBottom());
        } else if (gravity == Gravity.RIGHT) {
            wi = wi.replaceSystemWindowInsets(0, wi.getSystemWindowInsetTop(),
                    wi.getSystemWindowInsetRight(), wi.getSystemWindowInsetBottom());
        }
        lp.leftMargin = wi.getSystemWindowInsetLeft();
        lp.topMargin = wi.getSystemWindowInsetTop();
        lp.rightMargin = wi.getSystemWindowInsetRight();
        lp.bottomMargin = wi.getSystemWindowInsetBottom();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    public static int getTopInset(Object insets) {
        return insets != null ? ((WindowInsets) insets).getSystemWindowInsetTop() : 0;
    }

    public static Drawable getDefaultStatusBarBackground(Context context) {
        final TypedArray a = context.obtainStyledAttributes(THEME_ATTRS);
        try {
            return a.getDrawable(0);
        } finally {
            a.recycle();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    static class InsetsListener implements View.OnApplyWindowInsetsListener {
        @Override
        public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
            final DrawerLayoutImpl drawerLayout = (DrawerLayoutImpl) v;
            drawerLayout.setChildInsets(insets, insets.getSystemWindowInsetTop() > 0);
            return insets.consumeSystemWindowInsets();
        }
    }
}