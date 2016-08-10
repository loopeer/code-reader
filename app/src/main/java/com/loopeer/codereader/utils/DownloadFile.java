package com.loopeer.codereader.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DownloadFile {
  private static final String TAG = "Download";
  private static final int BUFFER_SIZE = 8192;

  /**
   * Download a file from a URL somewhere. The download is atomic; that is, it
   * downloads to a temporary file, then renames it to the requested file name
   * only if the download successfully completes.
   *
   * Returns TRUE if download succeeds, FALSE otherwise.
   *
   */
  public DownloadFile(Context context) {

  }

  /**
   * Copy from one stream to another. Throws IOException in the event of error
   * (for example, SD card is full)
   *
   * @param is
   *            Input stream.
   * @param os
   *            Output stream.
   * @param buffer
   *            Temporary buffer to use for copy.
   * @param bufferSize
   *            Size of temporary buffer, in bytes.
   */
  public static void copyStream(InputStream is, OutputStream os,
                                byte[] buffer, int bufferSize, String confid, double fileSize,
                                Context context) throws IOException {
    double downloaded = 0;
    int[] update = new int[3];

    //Intent intent = new Intent();
    // TODO
    //intent.setAction(Const.BROADCAST + confid);

    try {
      for (;;) {
        int count = is.read(buffer, 0, bufferSize);
        downloaded += count;
        if (count == -1) {
          if (context != null) {
            //intent.putExtra("zipcomplete", 1);
            //context.sendBroadcast(intent);
          }
          break;
        }
        os.write(buffer, 0, count);

        if (context != null) {
          update[0] = (int) downloaded;
          update[1] = (int) fileSize;
          update[2] = (int) ((downloaded / fileSize) * 100);
          //intent.putExtra("zipprogress", update);
          //context.sendBroadcast(intent);
        }
      }

    } catch (IOException e) {
      throw e;
    }
  }

  public static String humanReadableByteCount(long bytes, boolean si) {
    int unit = si ? 1000 : 1024;
    if (bytes < unit)
      return bytes + " B";
    int exp = (int) (Math.log(bytes) / Math.log(unit));
    String pre = (si ? "KMGTPE" : "KMGTPE").charAt(exp - 1)
        + (si ? "" : "i");
    return String.format("%.2f %sB", bytes / Math.pow(unit, exp), pre);
  }
}
