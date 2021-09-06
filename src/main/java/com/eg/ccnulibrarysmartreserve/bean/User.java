package com.eg.ccnulibrarysmartreserve.bean;

import com.alibaba.fastjson.JSON;

import java.net.HttpCookie;

public class User {
    private String username;
    private String password;
    private String dev_id;
    private HttpCookie cookie;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HttpCookie getCookie() {
        return cookie;
    }

    public void setCookie(HttpCookie cookie) {
        this.cookie = cookie;
    }

    public String getDev_id() {
        return dev_id;
    }

    public void setDev_id(String dev_id) {
        this.dev_id = dev_id;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
