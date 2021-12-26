package com.eg.ccnulibrarysmartreserve.bean.config;

import com.alibaba.fastjson.JSON;
import lombok.Data;

@Data
public class NotificationChannel {
    private String channel;
    private String content;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
