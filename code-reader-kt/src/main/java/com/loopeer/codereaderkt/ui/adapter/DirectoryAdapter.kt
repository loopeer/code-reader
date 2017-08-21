package com.loopeer.codereaderkt.ui.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.loopeer.codereaderkt.R
import com.loopeer.codereaderkt.model.DirectoryNode
import com.loopeer.codereaderkt.ui.view.DirectoryNavDelegate
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
            val clickListener = {
                if (var1.isDirectory) {
                    var1.openChild = !var1.openChild
                    updateData(adaptNodes())
                } else {
                    mFileClickListener.doOpenFile(var1)
                }
            }
            var3.itemView.setOnClickListener(clickListener)
        }
        (var3 as? CodeReadRepoHeaderViewHolder)?.bind(mNodeRoot)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = layoutInflater
        val view: View
        when (viewType) {
            R.layout.list_item_code_read_repo_header -> {
                view = inflater.inflate(R.layout.list_item_code_read_repo_header, parent, false)
                return CodeReadRepoHeaderViewHolder(view)
            }
            else -> {
                view = inflater.inflate(R.layout.list_item_directory, parent, false)
                return DirectoryViewHolder(view)
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

    internal inner class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        @BindView(R.id.text_directory_name)
        var mTextDirectoryName: TextView? = null
//        @BindView(R.id.img_directory_open_close)
        var mImgOpenClose: ImageView? = null

        lateinit var mNode: DirectoryNode

        init {
//            ButterKnife.bind(this, itemView)
        }

        fun bind(pathNode: DirectoryNode) {
            mNode = pathNode
            mTextDirectoryName!!.text = pathNode.displayName
            val params = itemView.layoutParams as RecyclerView.LayoutParams
            params.leftMargin = 20 * pathNode.depth
            mImgOpenClose!!.isSelected = mNode.openChild
            var drawableId = R.drawable.ic_directory_file
            if (pathNode.isDirectory) {
                drawableId = R.drawable.ic_directory_path
                mImgOpenClose!!.visibility = if (pathNode.pathNodes.isEmpty()) View.INVISIBLE else View.VISIBLE
            } else {
                mImgOpenClose!!.visibility = View.INVISIBLE
            }
            val drawable = ContextCompat.getDrawable(itemView.context, drawableId)
            mTextDirectoryName!!.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }

    }

    internal inner class CodeReadRepoHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//        @BindView(R.id.img_code_read_repo_type)
        var mImgRepoType: ImageView? = null
//        @BindView(R.id.text_code_read_repo_name)
        var mTextRepoName: TextView? = null

        init {
//            ButterKnife.bind(this, itemView)
        }

        fun bind(directoryNode: DirectoryNode) {
            mImgRepoType!!.setImageResource(if (directoryNode.isDirectory) R.drawable.ic_repo_white else R.drawable.ic_document_white)
            mTextRepoName!!.text = directoryNode.name
        }
    }
}

private fun View.setOnClickListener(clickListener: () -> Unit) {}
