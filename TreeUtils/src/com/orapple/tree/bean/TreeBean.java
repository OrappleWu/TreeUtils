package com.orapple.tree.bean;

import java.util.List;

public class TreeBean<T> {

    // 树的ID
    private String treeId;

    // 树的级别
    private int treeLeave;

    // 树节点的数据
    private T treeData;

    // 树的子节点数据
    private List<TreeBean<T>> childData;

    public String getTreeId() {
        return treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    public int getTreeLeave() {
        return treeLeave;
    }

    public void setTreeLeave(int treeLeave) {
        this.treeLeave = treeLeave;
    }

    public T getTreeData() {
        return treeData;
    }

    public void setTreeData(T treeData) {
        this.treeData = treeData;
    }

    public List<TreeBean<T>> getChildData() {
        return childData;
    }

    public void setChildData(List<TreeBean<T>> childData) {
        this.childData = childData;
    }

    @Override
    public String toString() {
        return "{treeId:" + treeId + ", treeLeave:" + treeLeave + ", treeData:" +
                treeData + ", childData:" + childData + "}";
    }
}
