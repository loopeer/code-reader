package com.loopeer.codereaderkt.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import com.loopeer.codereaderkt.CodeReaderApplication
import com.loopeer.codereaderkt.model.DirectoryNode
import java.io.File
import java.util.*


class FileCache {
    companion object {
        private val EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE"
        private var instance: FileCache? = null
        private val cachePath = Environment.getExternalStorageDirectory().toString() + "/CodeReaderKt/repo/"
        private var cacheDir: File? = null
        val cacheDirPath = "/repo/"

    }


    init {
        cacheDir = if (hasSDCard() && hasExternalStoragePermission(CodeReaderApplication.appContext)) {
            createFilePath(cachePath)
        } else {
            createFilePath("${CodeReaderApplication.appContext.cacheDir}$cacheDirPath")//字符串拼接
        }
    }

    fun createFilePath(filePath: String): File {
        return createFilePath(File(filePath))
    }

    private fun createFilePath(file: File): File {
        if (!file.exists()) {
            file.mkdirs()//mkdir()和mkdirs区别 前者只会建立一级目录，后者可建多级
        }
        return file
    }

    fun getInstance(): FileCache {
        if (null == instance) {
            instance = FileCache()
        }
        return instance as FileCache
    }

    fun hasSDCard(): Boolean = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    //是否存在SD卡

    fun getFileDirectoryNode(): DirectoryNode? {
        val file = File(cacheDir, "cardStackView")
        if (file.listFiles() == null || file.listFiles().isEmpty()) return null
        return getFileDirectory(file)
    }

    fun getCacheDir(): File? = cacheDir

    fun getRepoAbsolutePath(repoName: String): String =
            getCacheDir()!!.path + File.separator + repoName


    fun getFileDirectory(file: File): DirectoryNode? {
        val directoryNode = DirectoryNode()
        directoryNode.name = file.name
        directoryNode.absolutePath = file.absolutePath
        if (file.isDirectory){
            directoryNode.isDirectory = true
            directoryNode.pathNodes = ArrayList<DirectoryNode>()
            file.listFiles()
                    .filterNot { it.name.startsWith(".") }
                    .map { getFileDirectory(it) }
                    .forEach { (directoryNode.pathNodes as ArrayList<DirectoryNode>).add(it!!) }
            if (!directoryNode.pathNodes?.isEmpty()!!){
//                Collections.sort(directoryNode.pathNodes)
            }
        }
        return directoryNode
    }

    private fun hasExternalStoragePermission(context: Context): Boolean {
        val perm: Int = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION)
        return perm == PackageManager.PERMISSION_GRANTED//是否已经获取sd卡读写权限
    }

    fun deleteFilesByDirectory(directory: File?) {
        if (directory != null && directory.exists() && directory.list() != null) {
            for (item in directory.listFiles()) {
                if (item.isDirectory) {
                    deleteFilesByDirectory(item)
                } else {
                    item.delete()
                }
            }
        }
    }

}