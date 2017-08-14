package com.loopeer.codereaderkt.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import java.util.ArrayList


abstract class RecyclerViewAdapter<T>() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mContext: Context = null!!
    var mInflater: LayoutInflater
    protected var mData: ArrayList<T>? = null

    constructor(context: Context) : this() {
        this.mContext = context
        this.mInflater = LayoutInflater.from(context)
        this.mData = ArrayList<T>()
    }

    open fun updateData(data: List<T>) {
        this.setData(data)
        this.notifyDataSetChanged()
    }

    open fun setData(data: List<T>?) {
        this.mData!!.clear()
        if (data != null) {
            this.mData!!.addAll(data)
        }

    }

    open fun getLayoutInflater(): LayoutInflater {
        return this.mInflater
    }

    open fun getContext(): Context {
        return this.mContext
    }

    override open fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = this.getItem(position)
        this.bindView(data, position, holder)
    }

    abstract open fun bindView(var1: T, var2: Int, var3: RecyclerView.ViewHolder)

    open fun getItem(position: Int): T {
        return this.mData!![position]
    }

    override open fun getItemCount(): Int {
        return if (this.mData == null) 0 else this.mData!!.size
    }
}