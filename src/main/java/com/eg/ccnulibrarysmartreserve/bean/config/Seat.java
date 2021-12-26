package com.eg.ccnulibrarysmartreserve.bean.config;

import com.alibaba.fastjson.JSON;
import lombok.Data;

@Data
public class Seat {
    private String name;
    private String dev_id;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
