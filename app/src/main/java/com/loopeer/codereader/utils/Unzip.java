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

    private String mZipFile;
    private String mLocation;
    private byte[] mBuffer;
    private Context mContext;

    /**
     * Constructor.
     *
     * @param zipFile  Fully-qualified path to .zip file
     * @param location Fully-qualified path to folder where files should be written.
     *                 Path must have a trailing slash.
     */
    public Unzip(String zipFile, String location, Context context) {
        mZipFile = zipFile;
        mLocation = location;
        mBuffer = new byte[BUFFER_SIZE];
        mContext = context;
        dirChecker("");
    }

    public void DecompressZip() {
        FileInputStream fin = null;
        ZipInputStream zin = null;
        OutputStream fout = null;
        File outputDir = new File(mLocation);
        File tmp = null;
        try {
            fin = new FileInputStream(mZipFile);
            zin = new ZipInputStream(fin);
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                Log.d("Decompress", "Unzipping " + ze.getName());
                if (ze.isDirectory()) {
                    dirChecker(ze.getName());
                } else {
                    tmp = File.createTempFile("decomp", ".tmp", outputDir);
                    fout = new BufferedOutputStream(new FileOutputStream(tmp));
                    DownloadFile.copyStream(zin, fout, mBuffer, BUFFER_SIZE,
                            null, 0, null);

                    zin.closeEntry();
                    fout.close();
                    fout = null;
                    tmp.renameTo(new File(mLocation + ze.getName()));
                    tmp = null;
                }
            }
            zin.close();
            zin = null;
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
        File f = new File(mLocation, dir);

        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }
}