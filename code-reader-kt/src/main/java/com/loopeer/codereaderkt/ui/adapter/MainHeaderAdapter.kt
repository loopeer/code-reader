package com.loopeer.codereaderkt.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.loopeer.codereaderkt.Navigator
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.model.MainHeaderItem
import java.util.ArrayList


class MainHeaderAdapter(private val mContext: Context) : BaseAdapter() {
    private val mDatas: MutableList<MainHeaderItem>

    init {
        mDatas = ArrayList<MainHeaderItem>()
    }

    override fun getCount(): Int {
        return mDatas.size
    }

    override fun getItem(i: Int): Any {
        return mDatas[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, convertView: View, viewGroup: ViewGroup): View {
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

//        val textView = view.findViewById(R.id.text_grid_item) as TextView
//        val imageView = view.findViewById(R.id.img_grid_item) as ImageView
//
//        textView.setText(item.name)
//        imageView.setImageResource(item.icon)
    }

    fun updateData(items: List<MainHeaderItem>) {
        setData(items)
        notifyDataSetChanged()
    }

    fun setData(items: List<MainHeaderItem>) {
        mDatas.clear()
        mDatas.addAll(items)
    }
}
