package com.eg.ccnulibrarysmartreserve;

import java.util.Calendar;

public class Test {
    public static void main(String[] args) {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        int dayOfWeek = tomorrow.get(Calendar.DAY_OF_WEEK);
        dayOfWeek--;
        System.out.println(dayOfWeek);
    }
}
