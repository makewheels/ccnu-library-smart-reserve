package com.eg.ccnulibrarysmartreserve;

import cn.hutool.core.util.RandomUtil;
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
//    @Scheduled(cron = "55 59 17 ? * *")
    @Scheduled(cron = "0 40 18 ? * *")
    private void reserve() {
        System.out.println(System.currentTimeMillis());
        List<User> userList = new ArrayList<>();
        User me = new User();
        me.setUsername("2020180007");
        me.setPassword("q63zuQushMESw3V");
        userList.add(me);
        System.out.println(userList);
        for (User user : userList) {
//            new Thread(() -> handleEachUser(user)).start();
        }
    }

    private void handleEachUser(User user) {
        for (int i = 0; i < 20; i++) {
            HttpCookie cookie = reserveService.loginAndGetCookie(user.getUsername(), user.getPassword());
            user.setCookie(cookie);
            LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
            LocalDateTime end = LocalDateTime.now().plusDays(1).withHour(21).withMinute(0);
            ReserveResponse reserve = reserveService.reserve(user.getCookie(), "101700014",
                    start.toInstant(ZoneOffset.of("+8")).toEpochMilli(),
                    end.toInstant(ZoneOffset.of("+8")).toEpochMilli());
            System.out.println(reserve);
            int ret = reserve.getRet();
            //如果预约成功
            if (ret == 1) {
                System.out.println("预约成功" + RandomUtil.randomString(12));
                break;
            }
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
