package com.eg.ccnulibrarysmartreserve;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eg.ccnulibrarysmartreserve.bean.config.Seat;
import com.eg.ccnulibrarysmartreserve.bean.config.User;
import com.eg.ccnulibrarysmartreserve.bean.reserve.ReserveResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.net.HttpCookie;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class AutoReserveTask {
    @Resource
    private ReserveService reserveService;

    public static void main(String[] args) throws InterruptedException {
        new AutoReserveTask().manualRun();
    }

    private void manualRun() throws InterruptedException {
        reserveService = new ReserveService();
        this.reserve();
    }

    /**
     * 每天提前x秒开始
     */
    @Scheduled(cron = "55 59 17 ? * *")
    public void reserve() throws InterruptedException {
//        JSONObject config = JSON.parseObject(System.getenv("config"));

        File configFile = new File(AutoReserveTask.class.getResource("/example-config.json").getPath());
        String json = FileUtil.readUtf8String(configFile);

        JSONObject config = JSON.parseObject(json);
        JSONArray usersJSONArray = config.getJSONArray("users");
        List<User> users = JSON.parseArray(JSON.toJSONString(usersJSONArray), User.class);
        log.info("解析出用户列表：");
        log.info(JSON.toJSONString(users));

        for (User user : users) {
            Thread thread = new Thread(() -> {
                try {
                    handleEachUser(user);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }

    /**
     * 当预约成功时
     *
     * @param user
     * @param seat
     */
    private void onReserveSuccess(User user, Seat seat) {
        log.info("预约成功: username = " + user.getUsername() + ", seat.name = " + seat.getName()
                + ", seat.dev_id = " + seat.getDev_id());


    }

    /**
     * 预约多次未成功超次数
     *
     * @param user
     * @param seat
     */
    private void onReserveTimeout(User user, Seat seat) {
        log.info("预约多次未成功超次数: username = " + user.getUsername() + ", seat.name = " + seat.getName()
                + ", seat.dev_id = " + seat.getDev_id());

    }

    /**
     * 处理一个座位
     *
     * @param user
     * @param seat
     * @throws InterruptedException
     */
    private void handleEachSeat(User user, Seat seat) throws InterruptedException {
        String username = user.getUsername();
        //最大尝试次数
        for (int i = 1; i <= 70; i++) {
            String currentTimeString = DateTimeFormatter.ofPattern("HH:mm:ss.SSS").format(LocalDateTime.now());

            log.info(Thread.currentThread().getName() + " 开始预约第 " + i + " 次, "
                    + "currentTime = " + currentTimeString
                    + "username = " + username
                    + ", seat.name = " + seat.getName() + ", seat.dev_id = " + seat.getDev_id());
            //先搞定起始和结束时间
            LocalDateTime start = LocalDateTime.now().plusDays(1)
                    .withHour(seat.getStartHour()).withMinute(seat.getStartMinute());
            LocalDateTime end = LocalDateTime.now().plusDays(1)
                    .withHour(seat.getEndHour()).withMinute(seat.getEndMinute());
            log.info("startTime = " + start + ", endTime = " + end);
            //执行预约
            ReserveResponse reserveResponse = reserveService.reserve(user.getCookie(), seat.getDev_id(),
                    start.toInstant(ZoneOffset.of("+8")).toEpochMilli(),
                    end.toInstant(ZoneOffset.of("+8")).toEpochMilli());
            log.info("预约结果：username = " + username + ", seat.name = " + seat.getName()
                    + ", seat.dev_id = " + seat.getDev_id() + ", reserveResponse = "
                    + JSON.toJSONString(reserveResponse));
            //如果预约成功
            /**
             * @see ReserveService#reserve()
             */
            if (reserveResponse.getRet() == 1) {
                onReserveSuccess(user, seat);
                return;
            } else {
                //到这里说明是预约失败，原因有三种，详见ReserveService#reserve()注释
                //预约失败并不能直接return，因为可能是没到时间，也有低概率是被别人约了
                //这里ret值一样，就不做区分了
                //大概率是还没到预约时间，系统也并不是准时开放，所以才加了那么大的预约次数
                //那没到时间就需要，sleep等待，再开始下一轮尝试
                //所以这里不做处理也对，就是可能出现已被约过，但是还在反复发请求约
            }
            //还没到开放预约时间，稍作等待，再做尝试
            Thread.sleep(500);
        }
        //超时处理
        onReserveTimeout(user, seat);
    }

    /**
     * 处理一个用户
     *
     * @param user
     * @throws InterruptedException
     */
    private void handleEachUser(User user) throws InterruptedException {
        String username = user.getUsername();
        log.info("开始为用户预约: username = " + username);
        HttpCookie cookie = reserveService.loginAndGetCookie(username, user.getPassword());
        user.setCookie(cookie);
        List<Seat> seats = user.getSeats();
        for (Seat seat : seats) {
            handleEachSeat(user, seat);
        }
    }

}
