package com.eg.ccnulibrarysmartreserve.bean.config;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.net.HttpCookie;
import java.util.List;

@Data
public class User {
    private String username;
    private String password;

    private List<NotificationChannel> notificationChannels;
    private List<Seat> seats;

    private HttpCookie cookie;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
