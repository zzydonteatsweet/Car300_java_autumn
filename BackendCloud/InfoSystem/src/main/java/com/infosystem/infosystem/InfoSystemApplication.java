package com.infosystem.infosystem;

import com.infosystem.infosystem.demos.web.service.impl.MatchingServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InfoSystemApplication {

    public static void main(String[] args) {
        MatchingServiceImpl.matchingPool.start();  // 启动匹配线程
        SpringApplication.run(InfoSystemApplication.class, args);
    }

}
