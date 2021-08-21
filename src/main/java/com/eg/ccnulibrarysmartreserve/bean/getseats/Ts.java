package com.eg.ccnulibrarysmartreserve.bean.getseats;

import com.alibaba.fastjson.JSON;

import java.util.Date;

public class Ts {

    private String id;
    private String start;
    private String end;
    private String state;
    private Date date;
    private String name;
    private String title;
    private String owner;
    private String accno;
    private String member;
    private int limit;
    private boolean occupy;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStart() {
        return start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getEnd() {
        return end;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setAccno(String accno) {
        this.accno = accno;
    }

    public String getAccno() {
        return accno;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getMember() {
        return member;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public void setOccupy(boolean occupy) {
        this.occupy = occupy;
    }

    public boolean getOccupy() {
        return occupy;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
