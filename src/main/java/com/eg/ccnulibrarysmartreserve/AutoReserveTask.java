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
        me.setDev_id("101700061");
        userList.add(me);
        log.info("定时任务启动，用户列表：");
        for (User user : userList) {
            log.info(user.getUsername() + " ");
        }
        log.info("\n");
        for (User user : userList) {
            new Thread(() -> {
                try {
                    handleEachUser(user);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void handleEachUser(User user) throws InterruptedException {
        String username = user.getUsername();
        log.info("开始为用户预约: " + username);
        HttpCookie cookie = reserveService.loginAndGetCookie(username, user.getPassword());
        user.setCookie(cookie);
        for (int i = 1; i <= 70; i++) {
            log.info(Thread.currentThread().getName() + " 开始预约第 " + i + " 次 " + username);
            LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(30);
            LocalDateTime end = LocalDateTime.now().plusDays(1).withHour(22).withMinute(0);
            log.info("start = " + start + " end" + end);
            ReserveResponse reserve = reserveService.reserve(user.getCookie(), user.getDev_id(),
                    start.toInstant(ZoneOffset.of("+8")).toEpochMilli(),
                    end.toInstant(ZoneOffset.of("+8")).toEpochMilli());
            log.info(username + " " + JSON.toJSONString(reserve));
            //如果预约成功
            if (reserve.getRet() == 1) {
                log.info("预约成功: " + username);
                return;
            }
            Thread.sleep(700);
        }
        log.info("为指定用户预约超次数: " + username);
    }

}
