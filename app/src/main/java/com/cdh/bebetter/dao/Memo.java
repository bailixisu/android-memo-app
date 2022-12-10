package com.cdh.bebetter.dao;

import java.io.Serializable;
import java.util.Date;

public class Memo implements Serializable {
    Long id;
    private String content;
    private String startTime;
    private String deadline;
    private String completeTime;
    private String note;
    private String sort;
    private int color;
    private int status;
    private int like;
    private int circulate;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getCirculate() {
        return circulate;
    }

    public void setCirculate(int circulate) {
        this.circulate = circulate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Memo(String content, String startTime, String deadline) {
        this.content = content;
        this.startTime = startTime;
        this.deadline = deadline;
        id = new Date().getTime();
    }
    public Memo(){
        id = new Date().getTime();
        status = 0;
        like = 0;
        circulate = 0;
        color = 0xffffffff;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "Memo{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", startTime='" + startTime + '\'' +
                ", deadline='" + deadline + '\'' +
                ", completeTime='" + completeTime + '\'' +
                ", note='" + note + '\'' +
                ", sort='" + sort + '\'' +
                ", color=" + color +
                ", status=" + status +
                ", like=" + like +
                ", circulate=" + circulate +
                '}';
    }
}
