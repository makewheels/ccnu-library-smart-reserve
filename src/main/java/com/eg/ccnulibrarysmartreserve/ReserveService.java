package com.eg.ccnulibrarysmartreserve;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.eg.ccnulibrarysmartreserve.bean.getseats.GetSeatsResponse;
import com.eg.ccnulibrarysmartreserve.bean.reserve.ReserveResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * 预约工具类
 */
@Service
public class ReserveService {
    /**
     * 一键登录获取cookie
     *
     * @return ASP.NET_SessionId=2cjfvrazvsouc245hrlytu55
     */
    public HttpCookie loginAndGetCookie(String username, String password) {
        //获取account.ccnu.edu.cn下的lt参数
        HttpResponse accountPageResponse = HttpUtil.createGet(
                "https://account.ccnu.edu.cn/cas/login" +
                        "?service=http://kjyy.ccnu.edu.cn/loginall.aspx?page=").execute();
        HttpCookie JSESSIONID = accountPageResponse.getCookie("JSESSIONID");
        String lt = StringUtils.substringBetween(accountPageResponse.body(),
                "<input type=\"hidden\" name=\"lt\" value=\"",
                "\" />");

        //向account发送登录请求
        HttpResponse loginResponse = HttpUtil.createPost(
                        "https://account.ccnu.edu.cn/cas/login;jsessionid=" + JSESSIONID.getValue()
                                + "?service=http://kjyy.ccnu.edu.cn/loginall.aspx?page=")
                .cookie(JSESSIONID)
                .body("username=" + username + "&password=" + password
                        + "&lt=" + lt + "&execution=e1s1&_eventId=submit&submit=LOGIN").execute();
        String url = loginResponse.header("Location");
//        int status = loginResponse.getStatus();
//        System.out.println(status);
//        System.out.println(url);
        return HttpUtil.createGet(url).execute().getCookie("ASP.NET_SessionId");
    }

    /**
     * 获取座位信息
     */
    public GetSeatsResponse getSeats(HttpCookie cookie, String room_id, int dayOffset) {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(dayOffset);
        Date date = new Date(localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        String json = HttpUtil.createGet("http://kjyy.ccnu.edu.cn/ClientWeb/pro/ajax/device.aspx"
                        + "?byType=devcls&classkind=8&display=fp&md=d&room_id=" + room_id
                        + "&purpose=&selectOpenAty=&cld_name=default&date="
                        + new SimpleDateFormat("yyyy-MM-dd").format(date)
                        + "&fr_start=07%3A00&fr_end=22%3A00&act=get_rsv_sta&_="
                        + System.currentTimeMillis())
                .cookie(cookie).execute().body();
        return JSON.parseObject(json, GetSeatsResponse.class);
    }

    /**
     * 执行预约
     * <p>
     * 返回示例：
     * <p>
     * 没到预约开放时间：
     * {"act":"set_resv","msg":"2021-12-27要到[18:00]方可预约","ret":0}
     * <p>
     * 成功：
     * {"act":"set_resv","msg":"操作成功！","ret":1}
     * <p>
     * 自己约过再约：
     * {"act":"set_resv","msg":"2021-08-21您在【2021年08月21日】已有预约，当日不能再预约","ret":0}
     * <p>
     * 别人约过再约：
     * {"act":"set_resv","msg":"当前时间预约冲突","ret":0}
     */
    public ReserveResponse reserve(HttpCookie cookie, String dev_id, long start, long end) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdf2 = new SimpleDateFormat("Hmm");
        String json = HttpUtil.createGet("http://kjyy.ccnu.edu.cn/ClientWeb/pro/ajax/reserve.aspx"
                        + "?dialogid=&dev_id=" + dev_id + "&lab_id=&kind_id=&room_id="
                        + "&type=dev&prop=&test_id=&term=&Vnumber=&classkind="
                        + "&test_name="
//                        + "Smart Reserve " + URLUtil.encode(sdf1.format(new Date(start)))
                        + "&start=" + URLUtil.encode(sdf1.format(new Date(start)))
                        + "&end=" + URLUtil.encode(sdf1.format(new Date(end)))
                        + "&start_time=" + sdf2.format(start)
                        + "&end_time=" + sdf2.format(end) + "&up_file=&memo=&act=set_resv&_="
                        + System.currentTimeMillis())
                .cookie(cookie).execute().body();
        return JSON.parseObject(json, ReserveResponse.class);
    }

}