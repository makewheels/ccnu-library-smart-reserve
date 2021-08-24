package com.eg.ccnulibrarysmartreserve;

import com.eg.ccnulibrarysmartreserve.bean.User;
import com.eg.ccnulibrarysmartreserve.bean.reserve.ReserveResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.HttpCookie;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
public class AutoReserveTask {
    @Resource
    private ReserveService reserveService;

    /**
     * 每天提前5秒开始
     */
    @Scheduled(cron = "55 59 17 ? * *")
    private void reserve() {
        List<User> userList = new ArrayList<>();
        User me = new User();
        me.setUsername("2020180007");
        me.setPassword("q63zuQushMESw3V");
        userList.add(me);
        System.out.println("定时任务启动，用户列表：");
        for (User user : userList) {
            System.out.print(user.getUsername() + " ");
        }
        System.out.println();
        for (User user : userList) {
            new Thread(() -> handleEachUser(user)).start();
        }
    }

    private void handleEachUser(User user) {
        String username = user.getUsername();
        System.out.println("开始为用户预约: " + username);
        HttpCookie cookie = reserveService.loginAndGetCookie(username, user.getPassword());
        user.setCookie(cookie);
        for (int i = 0; i < 20; i++) {
            System.out.println("开始预约第 " + (i + 1) + " 次");
            LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
            LocalDateTime end = LocalDateTime.now().plusDays(1).withHour(21).withMinute(0);
            ReserveResponse reserve = reserveService.reserve(user.getCookie(), "101700014",
                    start.toInstant(ZoneOffset.of("+8")).toEpochMilli(),
                    end.toInstant(ZoneOffset.of("+8")).toEpochMilli());
            System.out.println(reserve);
            int ret = reserve.getRet();
            //如果预约成功
            if (ret == 1) {
                System.out.println("预约成功: " + user.getUsername());
                break;
            }
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("为指定用户预约超次数: " + user.getUsername());
    }

}
