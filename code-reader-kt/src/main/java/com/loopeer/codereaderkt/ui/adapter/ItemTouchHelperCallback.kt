package com.loopeer.codereaderkt.ui.adapter

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension


class ItemTouchHelperCallback : ItemTouchHelperExtension.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return ItemTouchHelperExtension.Callback.makeMovementFlags(0, ItemTouchHelper.START)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
//        if (viewHolder is MainLatestAdapter.RepoViewHolder)
//            (viewHolder as MainLatestAdapter.RepoViewHolder).mProgressRelativeLayout.setTranslationX(dX)
    }
}
