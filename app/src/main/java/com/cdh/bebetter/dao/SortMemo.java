package com.cdh.bebetter.dao;

public class SortMemo {
    private String sortText;
    private int sortIconColor;
    private int sortBackgroundColor;

    public String getSortText() {
        return sortText;
    }

    public SortMemo(String sortText, int sortIconColor) {
        this.sortText = sortText;
        this.sortIconColor = sortIconColor;
    }

    public int getSortBackgroundColor() {
        return sortBackgroundColor;
    }

    public void setSortBackgroundColor(int sortBackgroundColor) {
        this.sortBackgroundColor = sortBackgroundColor;
    }

    public SortMemo() {
    }

    public void setSortText(String sortText) {
        this.sortText = sortText;
    }

    public int getSortIconColor() {
        return sortIconColor;
    }

    public void setSortIconColor(int sortIconColor) {
        this.sortIconColor = sortIconColor;
    }


    @Override
    public String toString() {
        return "SortMemo{" +
                "sortText='" + sortText + '\'' +
                ", sortIconColor=" + sortIconColor +
                ", sortBackgroundColor=" + sortBackgroundColor +
                '}';
    }
}
