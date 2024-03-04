package com.infosystem.infosystem.demos.web.service.impl;

import com.infosystem.infosystem.demos.web.service.MatchingService;
import com.infosystem.infosystem.demos.web.service.impl.utils.MatchingPool;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {
    public final static MatchingPool matchingPool = new MatchingPool();

    @Override
    public String addRequest(String userId, String location) {
        System.out.println("add player: " + userId + " " + location);
//        matchingPool.addPlayer(userId, rating, botId);
        matchingPool.addRequest(userId, location);
        return "add Request success";
    }

}
