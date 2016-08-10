package com.loopeer.codereader.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzip {
  private static final int BUFFER_SIZE = 8192;

  private String _zipFile;
  private String _location;
  private byte[] _buffer;
  private Context _context;

  /**
   * Constructor.
   *
   * @param zipFile
   *            Fully-qualified path to .zip file
   * @param location
   *            Fully-qualified path to folder where files should be written.
   *            Path must have a trailing slash.
   */
  public Unzip(String zipFile, String location, Context context) {
    _zipFile = zipFile;
    _location = location;
    _buffer = new byte[BUFFER_SIZE];
    _context = context;
    dirChecker("");
  }

  public void DecompressZip() {
    FileInputStream fin = null;
    ZipInputStream zin = null;
    OutputStream fout = null;

    File outputDir = new File(_location);
    File tmp = null;

    // TODO
    //Intent intent = new Intent();
    //intent.setAction(Const.BROADCAST + _confid);
    try {
      fin = new FileInputStream(_zipFile);
      zin = new ZipInputStream(fin);
      ZipEntry ze = null;
      while ((ze = zin.getNextEntry()) != null) {
        Log.d("Decompress", "Unzipping " + ze.getName());

        if (ze.isDirectory()) {
          dirChecker(ze.getName());
        } else {
          tmp = File.createTempFile("decomp", ".tmp", outputDir);
          fout = new BufferedOutputStream(new FileOutputStream(tmp));
          DownloadFile.copyStream(zin, fout, _buffer, BUFFER_SIZE,
              null, 0, null);
          zin.closeEntry();
          fout.close();
          fout = null;
          tmp.renameTo(new File(_location + ze.getName()));
          tmp = null;
        }
      }
      zin.close();
      zin = null;
      //intent.putExtra("unzipcomplete", 1);
      //_context.sendBroadcast(intent);

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (tmp != null) {
        try {
          tmp.delete();
        } catch (Exception ignore) {
          ;
        }
      }
      if (fout != null) {
        try {
          fout.close();
        } catch (Exception ignore) {
          ;
        }
      }
      if (zin != null) {
        try {
          zin.closeEntry();
        } catch (Exception ignore) {
          ;
        }
      }
      if (fin != null) {
        try {
          fin.close();
        } catch (Exception ignore) {
          ;
        }
      }
    }
  }

  private void dirChecker(String dir) {
    File f = new File(_location, dir);

    if (!f.isDirectory()) {
      f.mkdirs();
    }
  }
}