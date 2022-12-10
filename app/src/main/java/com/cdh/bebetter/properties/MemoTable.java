package com.cdh.bebetter.properties;

public final class MemoTable {
    public static final String TABLE_NAME = "memo";
    public static final String DEADLINE = "deadline";
    public static final String START_TIME = "start_time";
    public static final String COMPLETE_TIME = "complete_time";
    public static final String CONTENT = "content";
    public static final String ID = "id";
    public static final String NOTE = "note";
    public static final String SORT = "sort";
    public static final String STATUS = "status";
    public static final String LIKE = "like";
    public static final String CIRCULATE = "circulate";
    public static final String TEt = "DROP TABLE memo;";
    public static final String COLOR = "color";
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS memo(\n" +
            "                        id INT PRIMARY KEY     NOT NULL,\n" +
            "                        content           TEXT    NOT NULL,\n" +
            "                        status           INT    NOT NULL,\n" +
            "                        complete_time            INT,\n" +
            "                        deadline        TEXT,\n" +
            "                        start_time         TEXT,\n" +
            "                        note         TEXT,\n" +
            "                        sort         TEXT,\n" +
            "                        like         INT,\n" +
            "                        circulate        INT,\n" +
            "                        color         INT\n" +
            ");";
}