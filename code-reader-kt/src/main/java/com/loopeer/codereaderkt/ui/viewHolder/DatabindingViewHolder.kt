package com.loopeer.codereaderkt.ui.viewHolder

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.View


open class DataBindingViewHolder<out T : ViewDataBinding>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding: T = DataBindingUtil.bind<T>(itemView)

}
