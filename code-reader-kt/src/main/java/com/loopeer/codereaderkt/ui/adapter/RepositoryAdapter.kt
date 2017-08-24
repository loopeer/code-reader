package com.loopeer.codereaderkt.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.loopeer.codereaderkt.Navigator
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.model.Repository


class RepositoryAdapter(context: Context) : RecyclerViewAdapters<Repository>(context) {

    private var mHasMore: Boolean = false

    fun setHasMore(hasMore: Boolean) {
        mHasMore = hasMore
    }

    override fun bindView(var1: Repository, var2: Int, var3: RecyclerView.ViewHolder?) {
        (var3 as? RepositoryViewHolder)?.bind(var1, var2)
    }

    override fun getItem(position: Int): Repository? =
            if (isFooterPositon(position)) null else super.getItem(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                R.layout.view_footer_loading -> {
                    val view = LayoutInflater.from(context).inflate(R.layout.view_footer_loading, parent, false)
                    object : RecyclerView.ViewHolder(view) {

                    }
                }
                else -> {
                    val view = LayoutInflater.from(context).inflate(R.layout.list_item_repository, parent, false)
                    RepositoryViewHolder(view)
                }
            }

    private fun isFooterPositon(position: Int): Boolean =
            mHasMore && position == itemCount - 1

    override fun getItemViewType(position: Int): Int =
            if (isFooterPositon(position)) R.layout.view_footer_loading else R.layout.list_item_repository

    override fun getItemCount(): Int = super.getItemCount() + if (mHasMore) 1 else 0

    internal inner class RepositoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var mImgAvatar: ImageView
        private var mTxtFullName: TextView
        private var mTxtDescription: TextView

        init {
            mImgAvatar = view.findViewById(R.id.img_avatar)
            mTxtFullName = view.findViewById(R.id.txt_full_name)
            mTxtDescription = view.findViewById(R.id.txt_description)
        }

        fun bind(repository: Repository, position: Int) {
            Glide.with(context).load(repository.owner?.avatarUrl).into(mImgAvatar)
            mTxtFullName.text = repository.fullName
            mTxtDescription.text = repository.description

            itemView.setOnClickListener { view ->
                Navigator().startWebActivity(context, repository.htmlUrl!!)
            }
        }
    }
}