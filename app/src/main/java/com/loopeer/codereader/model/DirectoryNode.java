package com.loopeer.codereader.model;

import java.util.List;

public class DirectoryNode extends BaseModel {

    public String name;
    public List<DirectoryNode> pathNodes;

    public DirectoryNode() {
    }

    public DirectoryNode(String name) {
        this.name = name;
    }

    public DirectoryNode(List<DirectoryNode> pathNodes, String name) {
        this.pathNodes = pathNodes;
        this.name = name;
    }
}
