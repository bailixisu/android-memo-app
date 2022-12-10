package com.cdh.bebetter.dao;

public class MyLocation {
    Long id;
    Double latitude;
    Double longitude;
    String time;

    public MyLocation(Double latitude, Double longitude, String time, Long id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.id = id;
    }

    public MyLocation() {
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

    @Override
    public String toString() {
        return "MyLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", time='" + time + '\'' +
                ", id=" + id +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
