package com.loopeer.codereaderkt.ui.adapter

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.loopeer.codereaderkt.model.Repo


class MainLatestAdapter() : RecyclerViewAdapter<Repo>(){
    constructor(parcel: Parcel) : this() {
    }

    constructor(context: Context) : this() {}

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun bindView(var1: Repo, var2: Int, var3: RecyclerView.ViewHolder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object CREATOR : Parcelable.Creator<MainLatestAdapter> {
        override fun createFromParcel(parcel: Parcel): MainLatestAdapter {
            return MainLatestAdapter(parcel)
        }

        override fun newArray(size: Int): Array<MainLatestAdapter?> {
            return arrayOfNulls(size)
        }
    }
}