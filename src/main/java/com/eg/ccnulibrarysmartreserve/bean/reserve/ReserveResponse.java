package com.eg.ccnulibrarysmartreserve.bean.reserve;

import com.alibaba.fastjson.JSON;

public class ReserveResponse {
    private int ret;
    private String act;
    private String msg;
    private String data;
    private String ext;

    public void setRet(int ret) {
        this.ret = ret;
    }

    public int getRet() {
        return ret;
    }

    public void setAct(String act) {
        this.act = act;
    }

    public String getAct() {
        return act;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getExt() {
        return ext;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
