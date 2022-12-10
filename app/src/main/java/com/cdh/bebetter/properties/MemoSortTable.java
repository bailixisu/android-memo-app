package com.cdh.bebetter.properties;

public class MemoSortTable {
    public static final String TABLE_NAME = "memo_sort";
    public static final String SORT_TEXT = "sort_text";
    public static final String SORT_ICON_COLOR = "sort_icon_color";
    public static final String SORT_BACKGROUND_COLOR = "sort_background_color";
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS memo_sort(\n" +
            "                        sort_text           TEXT  PRIMARY KEY  NOT NULL,\n" +
            "                        sort_icon_color           INT    NOT NULL,\n" +
            "                        sort_background_color           INT    NOT NULL\n" +
            ");";
}
