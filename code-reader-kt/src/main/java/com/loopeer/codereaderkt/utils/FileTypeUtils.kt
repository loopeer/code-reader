package com.loopeer.codereaderkt.utils

import java.util.HashMap

object FileTypeUtils {

    // comma separated list of all file extensions supported by the media scanner
    var sFileExtensions: String

    // Audio file types
    val FILE_TYPE_MP3 = 1
    val FILE_TYPE_M4A = 2
    val FILE_TYPE_WAV = 3
    val FILE_TYPE_AMR = 4
    val FILE_TYPE_AWB = 5
    val FILE_TYPE_WMA = 6
    val FILE_TYPE_OGG = 7
    private val FIRST_AUDIO_FILE_TYPE = FILE_TYPE_MP3
    private val LAST_AUDIO_FILE_TYPE = FILE_TYPE_OGG

    // MIDI file types
    val FILE_TYPE_MID = 11
    val FILE_TYPE_SMF = 12
    val FILE_TYPE_IMY = 13
    private val FIRST_MIDI_FILE_TYPE = FILE_TYPE_MID
    private val LAST_MIDI_FILE_TYPE = FILE_TYPE_IMY

    // Video file types
    val FILE_TYPE_MP4 = 21
    val FILE_TYPE_M4V = 22
    val FILE_TYPE_3GPP = 23
    val FILE_TYPE_3GPP2 = 24
    val FILE_TYPE_WMV = 25
    private val FIRST_VIDEO_FILE_TYPE = FILE_TYPE_MP4
    private val LAST_VIDEO_FILE_TYPE = FILE_TYPE_WMV

    // Image file types
    val FILE_TYPE_JPEG = 31
    val FILE_TYPE_GIF = 32
    val FILE_TYPE_PNG = 33
    val FILE_TYPE_BMP = 34
    val FILE_TYPE_WBMP = 35
    private val FIRST_IMAGE_FILE_TYPE = FILE_TYPE_JPEG
    private val LAST_IMAGE_FILE_TYPE = FILE_TYPE_WBMP

    // Playlist file types
    val FILE_TYPE_M3U = 41
    val FILE_TYPE_PLS = 42
    val FILE_TYPE_WPL = 43
    private val FIRST_PLAYLIST_FILE_TYPE = FILE_TYPE_M3U
    private val LAST_PLAYLIST_FILE_TYPE = FILE_TYPE_WPL

    // MarkDown file types
    val FILE_TYPE_MARKDOWN = 44
    val FILE_TYPE_MD = 45
    private val FIRST_MARKDOWNLIST_FILE_TYPE = FILE_TYPE_MARKDOWN
    private val LAST_MARKDOWNLIST_FILE_TYPE = FILE_TYPE_MD

    //静态内部类
    class MediaFileType(var fileType: Int, var mimeType: String)

    private val sFileTypeMap = HashMap<String, MediaFileType>()
    private val sMimeTypeMap = HashMap<String, Int>()

    internal fun addFileType(extension: String, fileType: Int, mimeType: String) {
        sFileTypeMap.put(extension, MediaFileType(fileType, mimeType))
        sMimeTypeMap.put(mimeType, fileType)
    }

    init {
        addFileType("MP3", FILE_TYPE_MP3, "audio/mpeg")
        addFileType("M4A", FILE_TYPE_M4A, "audio/mp4")
        addFileType("WAV", FILE_TYPE_WAV, "audio/x-wav")
        addFileType("AMR", FILE_TYPE_AMR, "audio/amr")
        addFileType("AWB", FILE_TYPE_AWB, "audio/amr-wb")
        addFileType("WMA", FILE_TYPE_WMA, "audio/x-ms-wma")
        addFileType("OGG", FILE_TYPE_OGG, "application/ogg")

        addFileType("MID", FILE_TYPE_MID, "audio/midi")
        addFileType("XMF", FILE_TYPE_MID, "audio/midi")
        addFileType("RTTTL", FILE_TYPE_MID, "audio/midi")
        addFileType("SMF", FILE_TYPE_SMF, "audio/sp-midi")
        addFileType("IMY", FILE_TYPE_IMY, "audio/imelody")

        addFileType("MP4", FILE_TYPE_MP4, "video/mp4")
        addFileType("M4V", FILE_TYPE_M4V, "video/mp4")
        addFileType("3GP", FILE_TYPE_3GPP, "video/3gpp")
        addFileType("3GPP", FILE_TYPE_3GPP, "video/3gpp")
        addFileType("3G2", FILE_TYPE_3GPP2, "video/3gpp2")
        addFileType("3GPP2", FILE_TYPE_3GPP2, "video/3gpp2")
        addFileType("WMV", FILE_TYPE_WMV, "video/x-ms-wmv")

        addFileType("JPG", FILE_TYPE_JPEG, "image/jpeg")
        addFileType("JPEG", FILE_TYPE_JPEG, "image/jpeg")
        addFileType("GIF", FILE_TYPE_GIF, "image/gif")
        addFileType("PNG", FILE_TYPE_PNG, "image/png")
        addFileType("BMP", FILE_TYPE_BMP, "image/x-ms-bmp")
        addFileType("WBMP", FILE_TYPE_WBMP, "image/vnd.wap.wbmp")

        addFileType("M3U", FILE_TYPE_M3U, "audio/x-mpegurl")
        addFileType("PLS", FILE_TYPE_PLS, "audio/x-scpls")
        addFileType("WPL", FILE_TYPE_WPL, "application/vnd.ms-wpl")

        addFileType("MD", FILE_TYPE_MD, "text/markdown")
        addFileType("MARKDOWN", FILE_TYPE_MARKDOWN, "text/markdown")

        // compute file extensions list for native Media Scanner
        val builder = StringBuilder()
        val iterator = sFileTypeMap.keys.iterator()

        while (iterator.hasNext()) {
            if (builder.length > 0) {
                builder.append(',')
            }
            builder.append(iterator.next())
        }
        sFileExtensions = builder.toString()
    }

    val UNKNOWN_STRING = "<unknown>"

    private fun isAudioFileType(fileType: Int): Boolean {
        return fileType >= FIRST_AUDIO_FILE_TYPE && fileType <= LAST_AUDIO_FILE_TYPE || fileType >= FIRST_MIDI_FILE_TYPE && fileType <= LAST_MIDI_FILE_TYPE
    }

    private fun isVideoFileType(fileType: Int): Boolean {
        return fileType >= FIRST_VIDEO_FILE_TYPE && fileType <= LAST_VIDEO_FILE_TYPE
    }

    private fun isImageFileType(fileType: Int): Boolean {
        return fileType >= FIRST_IMAGE_FILE_TYPE && fileType <= LAST_IMAGE_FILE_TYPE
    }

    private fun isPlayListFileType(fileType: Int): Boolean {
        return fileType >= FIRST_PLAYLIST_FILE_TYPE && fileType <= LAST_PLAYLIST_FILE_TYPE
    }

    private fun isMarkDownFileType(fileType: Int): Boolean {
        return fileType >= FIRST_MARKDOWNLIST_FILE_TYPE && fileType <= LAST_MARKDOWNLIST_FILE_TYPE
    }

    fun getFileType(path: String?): MediaFileType? {
        if(path!=null){
            val lastDot = path?.lastIndexOf(".")
            if (lastDot < 0)
                return null
            return sFileTypeMap[path.substring(lastDot + 1).toUpperCase()]
        }
        return null
    }

    fun isVideoFileType(path: String): Boolean {
        val type = getFileType(path)
        if (null != type) {
            return isVideoFileType(type.fileType)
        }
        return false
    }

    fun isAudioFileType(path: String): Boolean {
        val type = getFileType(path)
        if (null != type) {
            return isAudioFileType(type.fileType)
        }
        return false
    }

    fun isImageFileType(path: String?): Boolean {
        val type = getFileType(path)
        if (null != type) {
            return isImageFileType(type.fileType)
        }
        return false
    }

    fun getFileTypeForMimeType(mimeType: String): Int {
        val value = sMimeTypeMap[mimeType]
        return value?.toInt() ?: 0
    }

    fun isMdFileType(path: String?): Boolean {
        val type = getFileType(path)
        if (null != type) {
            return isMarkDownFileType(type.fileType)
        }
        return false
    }



}
