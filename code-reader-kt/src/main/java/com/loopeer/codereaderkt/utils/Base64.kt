package com.loopeer.codereaderkt.utils

import java.io.UnsupportedEncodingException

object Base64 {

    /** The equals sign (=) as a byte.  */
    private val EQUALS_SIGN = '='.toByte()

    /** Preferred encoding.  */
    private val PREFERRED_ENCODING = "US-ASCII"

    /** The 64 valid Base64 values.  */
    private val _STANDARD_ALPHABET = byteArrayOf('A'.toByte(), 'B'.toByte(), 'C'.toByte(), 'D'.toByte(), 'E'.toByte(), 'F'.toByte(), 'G'.toByte(), 'H'.toByte(), 'I'.toByte(), 'J'.toByte(), 'K'.toByte(), 'L'.toByte(), 'M'.toByte(), 'N'.toByte(), 'O'.toByte(), 'P'.toByte(), 'Q'.toByte(), 'R'.toByte(), 'S'.toByte(), 'T'.toByte(), 'U'.toByte(), 'V'.toByte(), 'W'.toByte(), 'X'.toByte(), 'Y'.toByte(), 'Z'.toByte(), 'a'.toByte(), 'b'.toByte(), 'c'.toByte(), 'd'.toByte(), 'e'.toByte(), 'f'.toByte(), 'g'.toByte(), 'h'.toByte(), 'i'.toByte(), 'j'.toByte(), 'k'.toByte(), 'l'.toByte(), 'm'.toByte(), 'n'.toByte(), 'o'.toByte(), 'p'.toByte(), 'q'.toByte(), 'r'.toByte(), 's'.toByte(), 't'.toByte(), 'u'.toByte(), 'v'.toByte(), 'w'.toByte(), 'x'.toByte(), 'y'.toByte(), 'z'.toByte(), '0'.toByte(), '1'.toByte(), '2'.toByte(), '3'.toByte(), '4'.toByte(), '5'.toByte(), '6'.toByte(), '7'.toByte(), '8'.toByte(), '9'.toByte(), '+'.toByte(), '/'.toByte())

    /**
     *
     *
     * Encodes up to three bytes of the array <var>source</var> and writes the
     * resulting four Base64 bytes to <var>destination</var>. The source and
     * destination arrays can be manipulated anywhere along their length by
     * specifying <var>srcOffset</var> and <var>destOffset</var>. This method
     * does not check to make sure your arrays are large enough to accomodate
     * <var>srcOffset</var> + 3 for the <var>source</var> array or
     * <var>destOffset</var> + 4 for the <var>destination</var> array. The
     * actual number of significant bytes in your array is given by
     * <var>numSigBytes</var>.
     *
     *
     *
     * This is the lowest level of the encoding methods with all possible
     * parameters.
     *

     * @param source
     * *          the array to convert
     * *
     * @param srcOffset
     * *          the index where conversion begins
     * *
     * @param numSigBytes
     * *          the number of significant bytes in your array
     * *
     * @param destination
     * *          the array to hold the conversion
     * *
     * @param destOffset
     * *          the index where output will be put
     * *
     * @return the <var>destination</var> array
     * *
     * @since 1.3
     */
    private fun encode3to4(source: ByteArray, srcOffset: Int,
                           numSigBytes: Int, destination: ByteArray, destOffset: Int): ByteArray {

        val ALPHABET = _STANDARD_ALPHABET
        val inBuff = (if (numSigBytes > 0) ((source[srcOffset] as Int).shl(24)).ushr(8) else 0) or (if (numSigBytes > 1) ((source[srcOffset + 1] as Int).shl(24)).ushr(16) else 0) or if (numSigBytes > 2) ((source[srcOffset + 2] as Int).shl(24)).ushr(24) else 0
        when (numSigBytes) {
            3 -> {
                destination[destOffset] = ALPHABET[inBuff.ushr(18)]
                destination[destOffset + 1] = ALPHABET[inBuff.ushr(12) and 0x3f]
                destination[destOffset + 2] = ALPHABET[inBuff.ushr(6) and 0x3f]
                destination[destOffset + 3] = ALPHABET[inBuff and 0x3f]
                return destination
            }

            2 -> {
                destination[destOffset] = ALPHABET[inBuff.ushr(18)]
                destination[destOffset + 1] = ALPHABET[inBuff.ushr(12) and 0x3f]
                destination[destOffset + 2] = ALPHABET[inBuff.ushr(6) and 0x3f]
                destination[destOffset + 3] = EQUALS_SIGN
                return destination
            }

            1 -> {
                destination[destOffset] = ALPHABET[inBuff.ushr(18)]
                destination[destOffset + 1] = ALPHABET[inBuff.ushr(12) and 0x3f]
                destination[destOffset + 2] = EQUALS_SIGN
                destination[destOffset + 3] = EQUALS_SIGN
                return destination
            }

            else -> return destination
        }
    }

    /**
     * Encode string as a byte array in Base64 annotation.

     * @param string
     * *
     * @return The Base64-encoded data as a string
     */
    fun encode(string: String): String {
        var bytes: ByteArray
        try {
            bytes = string.toByteArray(charset(PREFERRED_ENCODING))
        } catch (e: UnsupportedEncodingException) {
            bytes = string.toByteArray()
        }

        return encodeBytes(bytes)
    }

    /**
     * Encodes a byte array into Base64 notation.

     * @param source
     * *          The data to convert
     * *
     * @param off
     * *          Offset in array where conversion should begin
     * *
     * @param len
     * *          Length of data to convert
     * *
     * @return The Base64-encoded data as a String
     * *
     * @throws NullPointerException
     * *           if source array is null
     * *
     * @throws IllegalArgumentException
     * *           if source array, offset, or length are invalid
     * *
     * @since 2.0
     */
    @JvmOverloads fun encodeBytes(source: ByteArray, off: Int = 0, len: Int = source.size): String {
        val encoded = encodeBytesToBytes(source, off, len)
        try {
            return String(encoded, charset(PREFERRED_ENCODING))
        } catch (uue: UnsupportedEncodingException) {
            return String(encoded)
        }

    }




    /**
     * Similar to [.encodeBytes] but returns a byte
     * array instead of instantiating a String. This is more efficient if you're
     * working with I/O streams and have large data sets to encode.


     * @param source
     * *          The data to convert
     * *
     * @param off
     * *          Offset in array where conversion should begin
     * *
     * @param len
     * *          Length of data to convert
     * *
     * @return The Base64-encoded data as a String if there is an error
     * *
     * @throws NullPointerException
     * *           if source array is null
     * *
     * @throws IllegalArgumentException
     * *           if source array, offset, or length are invalid
     * *
     * @since 2.3.1
     */
    fun encodeBytesToBytes(source: ByteArray?, off: Int, len: Int): ByteArray {

        if (source == null)
            throw NullPointerException("Cannot serialize a null array.")

        if (off < 0)
            throw IllegalArgumentException("Cannot have negative offset: " + off)

        if (len < 0)
            throw IllegalArgumentException("Cannot have length offset: " + len)

        if (off + len > source.size)
            throw IllegalArgumentException(
                String
                    .format(
                        "Cannot have offset of %d and length of %d with array of length %d",
                        off, len, source.size))

        // Bytes needed for actual encoding
        val encLen = len / 3 * 4 + if (len % 3 > 0) 4 else 0

        val outBuff = ByteArray(encLen)

        var d = 0
        var e = 0
        val len2 = len - 2
        while (d < len2) {
            encode3to4(source, d + off, 3, outBuff, e)
            d += 3
            e += 4
        }

        if (d < len) {
            encode3to4(source, d + off, len - d, outBuff, e)
            e += 4
        }

        if (e <= outBuff.size - 1) {
            val finalOut = ByteArray(e)
            System.arraycopy(outBuff, 0, finalOut, 0, e)
            return finalOut
        } else
            return outBuff
    }
}
/** Defeats instantiation.  */
/**
 * Encodes a byte array into Base64 notation.

 * @param source
 * *          The data to convert
 * *
 * @return The Base64-encoded data as a String
 * *
 * @throws NullPointerException
 * *           if source array is null
 * *
 * @throws IllegalArgumentException
 * *           if source array, offset, or length are invalid
 * *
 * @since 2.0
 */

