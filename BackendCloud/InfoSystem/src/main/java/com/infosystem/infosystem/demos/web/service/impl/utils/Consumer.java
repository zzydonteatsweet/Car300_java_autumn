package com.infosystem.infosystem.demos.web.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Component
public class Consumer extends Thread{
    public static RedisTemplate<String,String> redisTemplate;
    private String requestUrl;
    private String num;
    private static RestTemplate restTemplate;
    private String res = "";
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        Consumer.restTemplate = restTemplate;
    }
    @Autowired
    public void setRedisTemplate(RedisTemplate<String,String> redisTemplate) { Consumer.redisTemplate = redisTemplate;}
    public void setTimeout(long timeOut, String num) {
        String url = "https://cx.shouji.360.cn/phonearea.php?number=" + num;
        this.num = num;
        requestUrl = url;
        this.start();
        try {
            this.join(timeOut);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.interrupt();
        }
    }

    @Override
    public void run() {
        int time = 0;
        while(time < 5 && res.length() == 0) {
            if(time > 0) {
                System.out.println("retry " + time);
            }
            res = restTemplate.getForObject(requestUrl, String.class);
            time ++;
        }
        if(res.length() != 0)
            redisTemplate.boundValueOps(num).set(res, 5, TimeUnit.MINUTES);
    }

}
