package com.loopeer.codereaderkt.utils;

import android.content.Context;

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

    private FileInputStream mfin;
    private String mLocation;
    private byte[] mBuffer;
    private Context mContext;

    /**
     * Constructor.
     *
     * @param fin  Fully-qualified path to .zip file
     * @param location Fully-qualified path to folder where files should be written.
     *                 Path must have a trailing slash.
     */
    public Unzip(FileInputStream fin, String location, Context context) {
        mfin=fin;
        mLocation = location;
        mBuffer = new byte[BUFFER_SIZE];
        mContext = context;
        dirChecker(null);
    }

    public void DecompressZip() {
        ZipInputStream zin = null;
        OutputStream fout = null;
        File outputDir = new File(mLocation);
        File tmp = null;
        if(mfin==null){
            return;
        }
        try {
            zin = new ZipInputStream(mfin);
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.isDirectory()) {
                    dirChecker(ze);
                } else {
                    tmp = File.createTempFile("decomp", ".tmp", outputDir);
                    fout = new BufferedOutputStream(new FileOutputStream(tmp));
                    DownloadFile.copyStream(zin, fout, mBuffer, BUFFER_SIZE,
                            null, 0, null);

                    zin.closeEntry();
                    fout.close();
                    fout = null;
                    tmp.renameTo(new File(getPathSaveName(ze)));
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
            if (mfin != null) {
                try {
                    mfin.close();
                } catch (Exception ignore) {
                    ;
                }
            }
        }
    }

    private String getPathSaveName(ZipEntry ze) {
        if (ze == null) {
            return mLocation;
        }
        String zeName = ze.getName();
        return mLocation + File.separator + zeName.substring(zeName.indexOf("/") + 1, zeName.length());
    }

    private void dirChecker(ZipEntry ze) {
        File f = new File(getPathSaveName(ze));
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }
}