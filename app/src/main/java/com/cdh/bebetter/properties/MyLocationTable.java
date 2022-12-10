package com.cdh.bebetter.properties;

public class MyLocationTable {
    public static final String TABLE_NAME = "my_location";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String TIME = "time";
    public static final String ID = "id";
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS my_location(\n" +
            "                        id       INT     PRIMARY KEY  NOT NULL,\n" +
            "                        longitude           REAL    NOT NULL,\n" +
            "                        time           TEXT    NOT NULL,\n" +
            "                        latitude           REAL    NOT NULL\n" +
            ");";

}
