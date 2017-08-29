package com.loopeer.codereaderkt.utils

import android.content.Context
import android.util.Log
import rx.exceptions.Exceptions

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class Unzip
/**
 * Constructor.

 * @param mfin  Fully-qualified path to .zip file
 * *
 * @param location Fully-qualified path to folder where files should be written.
 * *                 Path must have a trailing slash.
 */
(private val mfin: FileInputStream, private val mLocation: String?, private val mContext: Context) {
    private val mBuffer: ByteArray

    init {
        mBuffer = ByteArray(BUFFER_SIZE)
        dirChecker(null)
    }

    fun DecompressZip() {
        var zin: ZipInputStream? = null
        var fout: OutputStream? = null
        val outputDir = File(mLocation)
        var tmp: File? = null
        try {
            zin = ZipInputStream(mfin)
            var ze: ZipEntry?
            while (true) {
                ze=zin.nextEntry ?: break
                if (ze.isDirectory) {
                    dirChecker(ze)
                } else {
                    tmp = File.createTempFile("decomp", ".tmp", outputDir)
                    fout = BufferedOutputStream(FileOutputStream(tmp!!))
                    DownloadFile.copyStream(zin, fout, mBuffer, BUFFER_SIZE, null, 0.0, null)
                    zin.closeEntry()
                    fout.close()
                    fout = null
                    tmp.renameTo(File(getPathSaveName(ze)))
                    tmp = null
                }
            }
            zin.close()
            zin = null
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            if (tmp != null) {
                try {
                    tmp.delete()
                } catch (ignore: Exception) {
                }

            }
            if (fout != null) {
                try {
                    fout.close()
                } catch (ignore: Exception) {
                }

            }
            if (zin != null) {
                try {
                    zin.closeEntry()
                } catch (ignore: Exception) {
                }

            }
            if (mfin != null) {
                try {
                    mfin.close()
                } catch (ignore: Exception) {
                }

            }
        }
    }

    private fun getPathSaveName(ze: ZipEntry?): String? {
        if (ze == null) {
            return mLocation
        }
        val zeName = ze.name
        return mLocation + File.separator + zeName.substring(zeName.indexOf("/") + 1, zeName.length)
    }

    private fun dirChecker(ze: ZipEntry?) {
        val f = File(getPathSaveName(ze))
        if (!f.isDirectory) {
            f.mkdirs()
        }
    }

    companion object {
        private val BUFFER_SIZE = 8192
    }
}