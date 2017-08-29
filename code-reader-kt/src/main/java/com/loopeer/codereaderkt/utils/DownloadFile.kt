package com.loopeer.codereaderkt.utils

import android.content.Context

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class DownloadFile
/**
 * Download a file from a URL somewhere. The download is atomic; that is, it
 * downloads to a temporary file, then renames it to the requested file name
 * only if the download successfully completes.

 * Returns TRUE if download succeeds, FALSE otherwise.

 */
(context: Context) {
    companion object {
        private val TAG = "Download"
        private val BUFFER_SIZE = 8192

        /**
         * Copy from one stream to another. Throws IOException in the event of error
         * (for example, SD card is full)

         * @param is
         * *            Input stream.
         * *
         * @param os
         * *            Output stream.
         * *
         * @param buffer
         * *            Temporary buffer to use for copy.
         * *
         * @param bufferSize
         * *            Size of temporary buffer, in bytes.
         */
        @Throws(IOException::class)
        fun copyStream(`is`: InputStream, os: OutputStream,
                       buffer: ByteArray, bufferSize: Int, confid: String?, fileSize: Double,
                       context: Context?) {
            var downloaded = 0.0
            val update = IntArray(3)

            //Intent intent = new Intent();
            // TODO
            //intent.setAction(Const.BROADCAST + confid);

            try {
                while (true) {
                    val count = `is`.read(buffer, 0, bufferSize)
                    downloaded += count.toDouble()
                    if (count == -1) {
                        if (context != null) {
                            //intent.putExtra("zipcomplete", 1);
                            //context.sendBroadcast(intent);
                        }
                        break
                    }
                    os.write(buffer, 0, count)

                    if (context != null) {
                        update[0] = downloaded.toInt()
                        update[1] = fileSize.toInt()
                        update[2] = (downloaded / fileSize * 100).toInt()
                        //intent.putExtra("zipprogress", update);
                        //context.sendBroadcast(intent);
                    }
                }

            } catch (e: IOException) {
                throw e
            }

        }

        fun humanReadableByteCount(bytes: Long, si: Boolean): String {
            val unit = if (si) 1000 else 1024
            if (bytes < unit)
                return bytes.toString() + " B"
            val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
            val pre = (if (si) "KMGTPE" else "KMGTPE")[exp - 1] + if (si) "" else "i"
            return String.format("%.2f %sB", bytes / Math.pow(unit.toDouble(), exp.toDouble()), pre)
        }
    }
}
