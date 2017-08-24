package com.loopeer.codereaderkt.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import java.util.*


abstract class RecyclerViewAdapters<T>(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    var mData: MutableList<T> = ArrayList()

    fun updateData(data: List<T>) {
        this.setData(data)
        this.notifyDataSetChanged()
    }

    open fun setData(data: List<T>?) {
        this.mData.clear()
        if (data != null) {
            this.mData.addAll(data)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = this.getItem(position)
        if (data != null) {
            this.bindView(data, position, holder)
        }
    }

    abstract fun bindView(var1: T, var2: Int, var3: RecyclerView.ViewHolder?)

    open fun getItem(position: Int): T ? = this.mData[position]

    override fun getItemCount(): Int = this.mData.size
}