package com.eg.ccnulibrarysmartreserve;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpGlobalConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eg.ccnulibrarysmartreserve.bean.config.Seat;
import com.eg.ccnulibrarysmartreserve.bean.config.User;
import com.eg.ccnulibrarysmartreserve.bean.reserve.ReserveResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.net.HttpCookie;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
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
        HttpGlobalConfig.setTimeout(7000);
//        JSONObject config = JSON.parseObject(System.getenv("config"));

        File configFile = new File(AutoReserveTask.class.getResource("/example-config.json").getPath());
        String json = FileUtil.readUtf8String(configFile);

        JSONObject config = JSON.parseObject(json);
        JSONArray usersJSONArray = config.getJSONArray("users");
        List<User> users = JSON.parseArray(JSON.toJSONString(usersJSONArray), User.class);
        log.info("解析出用户列表：");
        log.info(JSON.toJSONString(users));

        //每个用户一个线程
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
     * <p>
     * 如果没到预约时间，不返回，在循环内转悠
     * 如果预约成功，返回true
     * 已被自己约过不做处理，当做没到时间，继续在循环内转悠
     * 已被别人约过返回false，用于父级调用者启用备选方案
     */
    private boolean handleEachSeat(User user, Seat seat) throws InterruptedException {
        String username = user.getUsername();
        //最大尝试次数
        for (int i = 1; i <= 70; i++) {
            String currentTimeString = DateTimeFormatter.ofPattern("HH:mm:ss.SSS").format(LocalDateTime.now());

            log.info(Thread.currentThread().getName() + " 开始预约第 " + i + " 次, "
                    + "currentTime = " + currentTimeString
                    + ", username = " + username
                    + ", seat.name = " + seat.getName() + ", seat.dev_id = " + seat.getDev_id());
            //先搞定起始和结束时间
            int endHour = seat.getEndHour();
            int endMinute = seat.getEndMinute();
            LocalDateTime start = LocalDateTime.now().plusDays(1)
                    .withHour(seat.getStartHour()).withMinute(seat.getStartMinute());
            LocalDateTime end = LocalDateTime.now().plusDays(1)
                    .withHour(endHour).withMinute(endMinute);

            //如果明天是周五，并且预订end时间的hour和minute超过14:00，那么hour和minute强制改成14:00
            //判断明天是不是星期五
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);
            int dayOfWeek = tomorrow.get(Calendar.DAY_OF_WEEK);
            dayOfWeek--;
            //如果是星期五
            if (dayOfWeek == 5) {
                //并且时间超过14:00
                if (endHour > 14 || (endHour == 14 && endMinute != 0)) {
                    end = end.withHour(14).withMinute(0);
                }
            }
            log.info("startTime = " + start + ", endTime = " + end);
            //执行预约
            ReserveResponse reserveResponse;
            try {
                reserveResponse = reserveService.reserve(user.getCookie(), seat.getDev_id(),
                        start.toInstant(ZoneOffset.of("+8")).toEpochMilli(),
                        end.toInstant(ZoneOffset.of("+8")).toEpochMilli());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            log.info("预约结果：username = " + username + ", seat.name = " + seat.getName()
                    + ", seat.dev_id = " + seat.getDev_id() + ", reserveResponse = "
                    + JSON.toJSONString(reserveResponse));
            //如果预约成功，返回true
            //@see ReserveService#reserve()
            if (reserveResponse.getRet() == 1) {
                onReserveSuccess(user, seat);
                return true;
            } else {
                //2022年3月15日20:17:30，杜继虎需求：seats备选list
                //首先，需要区分三种失败情况：1没到预约开放时间，2自己约过再约，3别人约过再约

                String msg = reserveResponse.getMsg();

                //2自己约过这种情况，认为不存在，不做处理
                if (StringUtils.equals(msg, "已有预约，当日不能再预约")) {

                    //1没到时间需要睡觉，继续往下执行循环就行，后面再约，再return，这里也不做处理
                } else if (StringUtils.equals(msg, "方可预约")) {

                    //3别人约过再约，也就是程序执行速度比手慢，这种情况是本次改造重点，
                } else if (StringUtils.equals(msg, "当前时间预约冲突")) {
                    //这个座位已经被占，返回预约失败，让更上一级启用备选座位
                    return false;
                }

            }
            //还没到开放预约时间，稍作等待，再做尝试
            Thread.sleep(500);
        }
        //超时
        onReserveTimeout(user, seat);
        //如果发生，从代码角度来说，原因未知，需要具体定位，这里先返回false，可用于启用备选seat
        return false;
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
            //预约一个用户的一个座位
            boolean isSeatSuccess = handleEachSeat(user, seat);
            //如果预约成功，就结束了。如果预约失败，继续循环，预约其它备选座位
            if (isSeatSuccess) {
                break;
            }
            //如果能到这里，说明上一个seat失败
            log.info("username = " + username + ", 上一个seat:dev_id = " + seat.getDev_id() + "失败，开始下一个");
        }
    }

}
