/**
 * Copyright 2021 json.cn
 */
package com.eg.ccnulibrarysmartreserve.bean.getseats;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Auto-generated: 2021-08-21 16:0:19
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class GetSeatsResponse {

    private int ret;
    private String act;
    private String msg;
    private List<Data> data;
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

    public void setData(List<Data> data) {
        this.data = data;
    }

    public List<Data> getData() {
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