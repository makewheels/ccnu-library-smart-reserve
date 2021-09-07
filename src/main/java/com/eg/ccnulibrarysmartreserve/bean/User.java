package com.eg.ccnulibrarysmartreserve.bean;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.net.HttpCookie;

@Data
public class User {
    private String username;
    private String password;
    private HttpCookie cookie;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
