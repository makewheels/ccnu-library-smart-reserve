package com.eg.ccnulibrarysmartreserve;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.StreamRequestHandler;

import java.io.InputStream;
import java.io.OutputStream;

public class AliyunRun implements StreamRequestHandler {
    private final AutoReserveTask autoReserveTask = new AutoReserveTask();

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) {
        try {
            autoReserveTask.reserve();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
