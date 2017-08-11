package com.loopeer.codereaderkt.model


class DirectoryNode{

    lateinit var name: String
    lateinit var pathNodes: List<DirectoryNode>
    var isDirectory: Boolean = false
    var openChild: Boolean = false
    var depth: Int = 0
    var displayName: String? = null
    var absolutePath: String? = null

    constructor(name: String) {
        this.name = name
    }

    constructor(pathNodes: List<DirectoryNode>, name: String) {
        this.pathNodes = pathNodes
        this.name = name
    }

    operator fun compareTo(o: Any): Int {
        if (o !is DirectoryNode) {
            return 1
        }
        if (o.isDirectory && !isDirectory) {
            return 1
        }
        if (!o.isDirectory && isDirectory) {
            return -1
        }
        return name.compareTo(o.name)
    }

    constructor()

}