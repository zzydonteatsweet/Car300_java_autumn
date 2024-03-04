package com.infosystem.infosystem.demos.web.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.infosystem.infosystem.demos.web.service.GetLocService;
import com.infosystem.infosystem.demos.web.service.impl.utils.Consumer;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
        if(hasNum(key))
            return redisTemplate.boundValueOps(key).get();
        else return "null";
    }
    public void setNum(String key, String value) {
        redisTemplate.boundValueOps(key).set(value, 5, TimeUnit.MINUTES);
    }

    @Override
    public String getLoc(String num) {
        System.out.println(num);
        if(num.length() != 11  || !StringUtils.isStrictlyNumeric(num)) return "number invalid";
        if(hasNum(num)) {
            return getNum(num);
        }

        Consumer consumer = new Consumer();
        consumer.setTimeout(2000, num);
        String s = getNum(num);
        JSONObject jsonObject = JSON.parseObject(s);
        String code = jsonObject.getString("code");
        if(!code.equals("0") || s.equals("null")) {
            setNum(num, "Invalid Number");
            return "Invalid Number";
        }

        JSONObject jsonObject_data = jsonObject.getJSONObject("data");
        String location = jsonObject_data.getString("province") +
                    jsonObject_data.getString("city")+
                    jsonObject_data.getString("sp");

        setNum(num, location);
        return location;
    }
}
