package com.loopeer.codereaderkt.ui.view

import android.app.Activity
import android.content.Context
import android.view.View
import java.util.HashMap


class ThemeChooser(private val mContext: Context, private val mOnItemSelectListener: OnItemSelectListener) {
    interface OnItemSelectListener {
        fun onItemSelect(id: Int, tag: String)
    }

    private val mViewThemeTags: HashMap<Int, String>

    init {
        mViewThemeTags = HashMap<Int, String>()
    }

    fun addItem(id: Int, tag: String) {
        mViewThemeTags.put(id, tag)
    }

    fun onItemSelect(view: View) {
        view.isSelected = true
        mOnItemSelectListener.onItemSelect(view.id, mViewThemeTags[view.id] as String)
        mViewThemeTags.keys
                .filter { view.id != it }
//                .forEach { (mContext as Activity).findViewById(it).setSelected(false) }
    }

    fun onItemSelectByTag(tag: String) {
        for ((id, value) in mViewThemeTags) {
//            (mContext as Activity).findViewById(id).setSelected(value == tag)
        }
    }

}
