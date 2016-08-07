package com.loopeer.directorychooser;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class FileNod implements Serializable{

    public String name;
    public String absolutePath;
    public String time;
    public String type;
    public long size;
    public String remark;
    public boolean isFolder;
    public List<FileNod> childNodes;

    public static Comparator<FileNod> NAME_COMPARATOR = new Comparator<FileNod>() {
        @Override
        public int compare(FileNod fileNod, FileNod t1) {
            if (fileNod.isFolder && !t1.isFolder) {
                return -1;
            }
            if (!fileNod.isFolder && t1.isFolder) {
                return 1;
            }
            return fileNod.name.compareTo(t1.name);
        }
    };

}
