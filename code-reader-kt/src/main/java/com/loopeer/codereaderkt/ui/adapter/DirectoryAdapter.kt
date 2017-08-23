package com.loopeer.codereaderkt.ui.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.databinding.ListItemCodeReadRepoHeaderBinding
import com.loopeer.codereaderkt.databinding.ListItemDirectoryBinding
import com.loopeer.codereaderkt.model.DirectoryNode
import com.loopeer.codereaderkt.ui.view.DirectoryNavDelegate
import com.loopeer.codereaderkt.ui.viewHolder.DataBindingViewHolder
import java.util.*


class DirectoryAdapter(context: Context, private val mFileClickListener: DirectoryNavDelegate.FileClickListener) : RecyclerViewAdapter<DirectoryNode>(context) {

    private lateinit var mNodeRoot: DirectoryNode

    var nodeRoot: DirectoryNode
        get() = mNodeRoot
        set(root) {
            mNodeRoot = root
            updateData(adaptNodes())
        }

    private fun adaptNodes(): List<DirectoryNode> {
        val nodes = ArrayList<DirectoryNode>()
        if (mNodeRoot.isDirectory) {
            val sb = StringBuilder()
            createShowNodes(nodes, -1, mNodeRoot, sb)
        } else {
            mNodeRoot.displayName = mNodeRoot.name
            nodes.add(mNodeRoot)
        }
        return nodes
    }

    private fun createShowNodes(nodes: ArrayList<DirectoryNode>, i: Int, nodeRoot: DirectoryNode, sb: StringBuilder) {
        var i = i
        ++i
        if (!nodeRoot.pathNodes.isEmpty()) {
            if (nodes.size != 0 && nodeRoot.pathNodes.size == 1 && nodeRoot.pathNodes[0].isDirectory) {
                nodeRoot.openChild = true
                nodes.removeAt(nodes.size - 1)
                --i
                val node = nodeRoot.pathNodes[0]
                node.depth = i
                sb.append(".")
                sb.append(node.name)
                node.displayName = sb.toString()
                nodes.add(node)
                if (node.openChild || node.pathNodes != null
                        && node.pathNodes.size == 1
                        && node.pathNodes[0].isDirectory) {
                    createShowNodes(nodes, i, node, sb)
                }
            } else {
                for (node in nodeRoot.pathNodes) {
                    if (sb.length > 0) sb.delete(0, sb.length)
                    node.depth = i
                    sb.append(node.name)
                    node.displayName = sb.toString()
                    nodes.add(node)
                    if (node.openChild || node.pathNodes != null
                            && node.pathNodes.size == 1
                            && node.pathNodes[0].isDirectory) {
                        createShowNodes(nodes, i, node, sb)
                    }
                }
            }
        }
    }

    override fun bindView(var1: DirectoryNode, var2: Int, var3: RecyclerView.ViewHolder) {
        if (var3 is DirectoryViewHolder) {
            var3.bind(var1)
            val clickListener = View.OnClickListener{
                if (var1.isDirectory) {
                    var1.openChild = !var1.openChild
                    updateData(adaptNodes())
                } else {
                    mFileClickListener.doOpenFile(var1)
                }
            }
            var3.mBinding.root.setOnClickListener(clickListener)
        }
        (var3 as? CodeReadRepoHeaderViewHolder)?.bind(mNodeRoot)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = layoutInflater
        when (viewType) {
            R.layout.list_item_code_read_repo_header -> {
                var mBingding=DataBindingUtil.inflate<ListItemCodeReadRepoHeaderBinding>(inflater,R.layout.list_item_code_read_repo_header, parent, false)
                return CodeReadRepoHeaderViewHolder(mBingding)
            }
            else -> {
                var mBingding=DataBindingUtil.inflate<ListItemDirectoryBinding>(inflater,R.layout.list_item_directory, parent, false)
                return DirectoryViewHolder(mBingding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            R.layout.list_item_code_read_repo_header
        } else R.layout.list_item_directory
    }

    override fun getItemCount(): Int {
        return if (super.getItemCount() == 0) 0 else super.getItemCount() + 1
    }

    override fun getItem(position: Int): DirectoryNode {
        return if (position == 0) mNodeRoot else super.getItem(position - 1)
    }

    internal inner class DirectoryViewHolder(mBinding: ListItemDirectoryBinding) : RecyclerView.ViewHolder(mBinding.root) {
        var mBinding:ListItemDirectoryBinding

        lateinit var mNode: DirectoryNode

        init {
            this.mBinding=mBinding
        }

        fun bind(pathNode: DirectoryNode) {
            mNode = pathNode
            mBinding.textDirectoryName.text = pathNode.displayName
            val params = itemView.layoutParams as RecyclerView.LayoutParams
            params.leftMargin = 20 * pathNode.depth
            mBinding.imgDirectoryOpenClose.isSelected = mNode.openChild
            var drawableId = R.drawable.ic_directory_file
            if (pathNode.isDirectory) {
                drawableId = R.drawable.ic_directory_path
                mBinding.imgDirectoryOpenClose!!.visibility = if (pathNode.pathNodes.isEmpty()) View.INVISIBLE else View.VISIBLE
            } else {
                mBinding.imgDirectoryOpenClose.visibility = View.INVISIBLE
            }
            val drawable = ContextCompat.getDrawable(itemView.context, drawableId)
            mBinding.textDirectoryName.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }

    }





    internal inner class CodeReadRepoHeaderViewHolder(mBinding: ListItemCodeReadRepoHeaderBinding) : RecyclerView.ViewHolder(mBinding.root){

        var mBinding:ListItemCodeReadRepoHeaderBinding
        init {
            this.mBinding=mBinding
        }

        fun bind(directoryNode: DirectoryNode) {
            if(directoryNode!=null){
                mBinding.imgCodeReadRepoType?.setImageResource(if (directoryNode.isDirectory) R.drawable.ic_repo_white else R.drawable.ic_document_white)
                mBinding.textCodeReadRepoName?.text = directoryNode.name
            }

        }
    }
}


