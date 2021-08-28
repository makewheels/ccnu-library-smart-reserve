package com.eg.ccnulibrarysmartreserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CcnuLibrarySmartReserveApplication {

    public static void main(String[] args) {
        SpringApplication.run(CcnuLibrarySmartReserveApplication.class, args);
    }

}
