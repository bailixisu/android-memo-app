package com.cdh.bebetter.dao;

import java.util.Date;

public class MyLocation {
    Long id;
    Double latitude;
    Double longitude;
    String time;
    Long memoId;

    public MyLocation(Double latitude, Double longitude, String time, Long memoId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.memoId = memoId;
        this.id = new Date().getTime();
    }

    public Long getMemoId() {
        return memoId;
    }

    public void setMemoId(Long memoId) {
        this.memoId = memoId;
    }

    public MyLocation() {
        this.id = new Date().getTime();
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MyLocation{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", time='" + time + '\'' +
                ", memoId=" + memoId +
                '}';
    }
}
