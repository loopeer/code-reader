package com.loopeer.codereaderkt.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.model.MainHeaderItem


class MainHeaderAdapter() : BaseAdapter() {
    private var mDatas: ArrayList<MainHeaderItem> = ArrayList<MainHeaderItem>()
    private lateinit var mContext: Context

    constructor(context: Context) : this() {
        Log.d("MainHeaderAdapterLog", "con")
        mContext = context
    }

    override fun getCount(): Int = mDatas.size

    override fun getItem(i: Int): Any = mDatas[i]

    override fun getItemId(i: Int): Long = i.toLong()

    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup): View {
        val view = LayoutInflater.from(mContext).inflate(R.layout.grid_item_main_header, viewGroup, false)
        bindView(mDatas[i], view)
        bindClick(view, mDatas[i], i)
        return view
    }

    private fun bindClick(view: View, item: MainHeaderItem, i: Int) {
        view.setOnClickListener { view1 ->
            when (i) {
//                0 -> Navigator.startSearchActivity(mContext)
//                1 -> Navigator.startWebActivity(mContext, item.link)
            }
        }
    }

    private fun bindView(item: MainHeaderItem, view: View) {
        Log.d("MainHeaderAdapterLog", "bindView")
        val textView = view.findViewById<TextView>(R.id.text_grid_item) as TextView
        val imageView = view.findViewById<ImageView>(R.id.img_grid_item) as ImageView

        textView.setText(item.name)
        imageView.setImageResource(item.icon)
    }

    fun updateData(items: List<MainHeaderItem>) {
        setData(items)
        notifyDataSetChanged()
    }

    private fun setData(items: List<MainHeaderItem>) {
        mDatas.clear()
        mDatas.addAll(items)
        Log.d("MainHeaderAdapterLog", "setData" + mDatas.size)
    }
}
