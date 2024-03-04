package com.infosystem.infosystem.demos.web.service.impl.utils;

import com.infosystem.infosystem.demos.web.service.GetLocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread {
    private static List<String[]> requestments = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private static RestTemplate restTemplate;
    private final static String responseLocUrl = "http://127.0.0.1:3000/response/";

    public static GetLocService getLocService;
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        MatchingPool.restTemplate = restTemplate;
    }

    @Autowired
    public void setGetLocService(GetLocService getLocService) {
        MatchingPool.getLocService = getLocService;
    }

    public void addRequest(String userId, String location) {
        lock.lock();
        try {
            String []tmp = {userId,location};
            requestments.add(tmp);
        } finally {
            lock.unlock();
        }
        System.out.println("Request added");
    }

    private void sendResult(String userId, String location) {  // 返回匹配结果
        System.out.println("send result: " + userId + " " + location);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("userId", userId);
        data.add("location",location);
        restTemplate.postForObject(responseLocUrl, data, String.class);
    }


    @Override
    public void run() {
        while (true) {
            if(requestments.size() > 0) {
                System.out.println("Here");
                lock.lock();
                System.out.println("There");
                try {
                    String []tmp = requestments.remove(0);
                    for(String p : tmp) System.out.println("requirements " + p);
                    String location = getLocService.getLoc(tmp[1]);
                    System.out.println("information got " + location);
                    sendResult(tmp[0],location);
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}
