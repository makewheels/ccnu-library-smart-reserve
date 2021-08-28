package com.eg.ccnulibrarysmartreserve;

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
