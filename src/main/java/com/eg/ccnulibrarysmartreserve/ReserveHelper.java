package com.eg.ccnulibrarysmartreserve;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import org.apache.commons.lang3.StringUtils;

import java.net.HttpCookie;

/**
 * 预约工具类
 */
public class ReserveHelper {
    /**
     * 一键获取预约系统登录cookie
     *
     * @return ASP.NET_SessionId=2cjfvrazvsouc245hrlytu55
     */
    public HttpCookie oneKeyGetCookie(String username, String password) {
        //获取account.ccnu.edu.cn下的lt参数
        HttpResponse accountPageresponse = HttpUtil.createGet("https://account.ccnu.edu.cn/cas/login" +
                "?service=http://kjyy.ccnu.edu.cn/loginall.aspx?page=").execute();
        HttpCookie JSESSIONID = accountPageresponse.getCookie("JSESSIONID");
        String lt = StringUtils.substringBetween(accountPageresponse.body(),
                "<input type=\"hidden\" name=\"lt\" value=\"",
                "\" />");

        //向account发送登录请求
        HttpResponse loginResponse = HttpUtil.createPost("https://account.ccnu.edu.cn/cas/login;jsessionid=" +
                        JSESSIONID.getValue() + "?service=http://kjyy.ccnu.edu.cn/loginall.aspx?page=")
                .cookie(JSESSIONID)
                .body("username=" + username + "&password=" + password
                        + "&lt=" + lt + "&execution=e1s1&_eventId=submit&submit=LOGIN").execute();
        int status = loginResponse.getStatus();
        String url = loginResponse.header("Location");
        System.out.println(status);
        System.out.println(url);
        return HttpUtil.createGet(url).execute().getCookie("ASP.NET_SessionId");
    }
    public String getSeats(){
        HttpUtil.createGet("http://kjyy.ccnu.edu.cn/ClientWeb/pro/ajax/device.aspx?byType=devcls&classkind=8&display=fp&md=d&room_id=101699189&purpose=&selectOpenAty=&cld_name=default&date=2021-08-21&fr_start=10%3A10&fr_end=10%3A40&act=get_rsv_sta&_=1629470370077")
    }

    public static void main(String[] args) {
    }
}
