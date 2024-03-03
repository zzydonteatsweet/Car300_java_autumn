package com.demo.demo.demos.web.Service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.demo.demo.demos.web.Service.GetLocService;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

@Service
public class GetLocServiceImpl implements GetLocService {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public void delete(String key) {
        redisTemplate.delete(key);
    }
    public boolean hasNum(String key) {
        if(Boolean.TRUE.equals(redisTemplate.hasKey(key))) return true;
        return false;
    }

    public String getNum(String key) {
        return redisTemplate.boundValueOps(key).get();
    }
    public void setNum(String key, String value) {
//        redisTemplate.boundValueOps(key).set(value);
        redisTemplate.boundValueOps(key).set(value, 5, TimeUnit.MINUTES);
    }

    @Override
    public String getLoc(String num) {
        if(num.length() != 11  || !StringUtils.isStrictlyNumeric(num)) return "number invalid";
        if(hasNum(num)) {
            return getNum(num);
        }
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://cx.shouji.360.cn/phonearea.php?number="+num;
        String s = restTemplate.getForObject(url, String.class);

        JSONObject jsonObject = JSON.parseObject(s);
        String code = jsonObject.getString("code");
        if(!code.equals("0")) {
            setNum(num, "Invalid Number");
            return "Invalid Number";
        }

        JSONObject jsonObject_data = jsonObject.getJSONObject("data");
        String location = jsonObject_data.getString("province") +
                    jsonObject_data.getString("city")+
                    jsonObject_data.getString("sp");
//        try {
//            byte[] utf_8 = location.getBytes("UTF-8");
//            location = new String(utf_8, "UTF-8");
//        }catch (UnsupportedEncodingException e) {
//            throw new RuntimeException(e);
//        } ;
        setNum(num, location);
        return location;
    }
}
