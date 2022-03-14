package com.eg.ccnulibrarysmartreserve;

public class TestMultiThreads {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 2; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    System.out.println(Thread.currentThread().getName() + " " + j);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }

    }
}
