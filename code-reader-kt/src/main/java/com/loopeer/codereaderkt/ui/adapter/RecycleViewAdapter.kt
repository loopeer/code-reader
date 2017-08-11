package com.loopeer.codereaderkt.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import java.util.ArrayList


abstract class RecyclerViewAdapter<T>() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var layoutInflater: LayoutInflater
    protected var mData: MutableList<T>? = null

    constructor(context: Context) : this() {
        this.layoutInflater = LayoutInflater.from(context)
        this.mData = ArrayList<T>()
    }

    fun updateData(data: List<T>) {
        this.setData(data)
        this.notifyDataSetChanged()
    }

    fun setData(data: List<T>?) {
        this.mData!!.clear()
        if (data != null) {
            this.mData!!.addAll(data)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = this.getItem(position)
        this.bindView(data, position, holder)
    }

    abstract fun bindView(var1: T, var2: Int, var3: RecyclerView.ViewHolder)

    fun getItem(position: Int): T {
        return this.mData!![position]
    }

    override fun getItemCount(): Int {
        return if (this.mData == null) 0 else this.mData!!.size
    }
}