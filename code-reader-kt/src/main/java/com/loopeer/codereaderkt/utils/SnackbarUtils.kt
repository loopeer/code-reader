package com.loopeer.codereaderkt.utils

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View


object SnackbarUtils {

    fun show(view: View, s: String) {
        Snackbar.make(view, s, Snackbar.LENGTH_SHORT).show()
    }

    fun show(view: View, @StringRes id: Int) {
        Snackbar.make(view, id, Snackbar.LENGTH_SHORT).show()
    }
}