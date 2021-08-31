package com.eg.ccnulibrarysmartreserve;

import com.alibaba.fastjson.JSON;
import com.eg.ccnulibrarysmartreserve.bean.User;
import com.eg.ccnulibrarysmartreserve.bean.reserve.ReserveResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.HttpCookie;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class AutoReserveTask {
    @Resource
    private ReserveService reserveService;

    /**
     * 每天提前5秒开始
     */
    @Scheduled(cron = "57 59 17 ? * *")
    private void reserve() {
        List<User> userList = new ArrayList<>();
        User me = new User();
        me.setUsername("2020180007");
        me.setPassword("q63zuQushMESw3V");
        userList.add(me);
        log.info("定时任务启动，用户列表：");
        for (User user : userList) {
            log.info(user.getUsername() + " ");
        }
        log.info("\n");
        for (User user : userList) {
            new Thread(() -> handleEachUser(user)).start();
        }
    }

    private void handleEachUser(User user) {
        String username = user.getUsername();
        log.info("开始为用户预约: " + username);
        HttpCookie cookie = reserveService.loginAndGetCookie(username, user.getPassword());
        user.setCookie(cookie);
        for (int i = 1; i <= 40; i++) {
            log.info(Thread.currentThread().getName() + " 开始预约第 " + i + " 次 " + username);
            LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
            LocalDateTime end = LocalDateTime.now().plusDays(1).withHour(21).withMinute(0);
            log.info("start = " + start + " end" + end);
            ReserveResponse reserve = reserveService.reserve(user.getCookie(), "101700041",
                    start.toInstant(ZoneOffset.of("+8")).toEpochMilli(),
                    end.toInstant(ZoneOffset.of("+8")).toEpochMilli());
            log.info(username + " " + JSON.toJSONString(reserve));
            //如果预约成功
            if (reserve.getRet() == 1) {
                log.info("预约成功: " + username);
                break;
            }
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("为指定用户预约超次数: " + username);
    }

}
