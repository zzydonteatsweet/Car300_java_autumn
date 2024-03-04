package com.backend.backend.demos.web.controller.num;

import com.backend.backend.demos.web.consumer.WebSocketServer;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReceiveLocResponseController {
    @PostMapping("/response/")
    public String receiveResponse(@RequestParam MultiValueMap<String, String> data) {
        String userId = data.getFirst("userId");
        String location = data.getFirst("location");
        System.out.println("get " + userId + " " + location);
        WebSocketServer.send2client(Integer.parseInt(userId), location);
        return "success";
    }

}
