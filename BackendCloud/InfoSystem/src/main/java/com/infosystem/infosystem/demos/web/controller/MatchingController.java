package com.infosystem.infosystem.demos.web.controller;

import com.infosystem.infosystem.demos.web.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchingController {
    @Autowired
    MatchingService matchingService;
    @PostMapping("/request/add/")
    public String addPlayer(@RequestParam MultiValueMap<String, String> data) {
        String userId = data.getFirst("userId");
        String location = data.getFirst("location");
        System.out.println("Received data "+ userId + " " + location);
        return matchingService.addRequest(userId, location);
    }
}
