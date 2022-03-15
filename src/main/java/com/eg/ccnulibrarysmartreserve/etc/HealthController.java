package com.eg.ccnulibrarysmartreserve.etc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HealthController {
    @RequestMapping("healthCheck")
    @ResponseBody
    public String healthCheck() {
        return "ok";
    }
}
