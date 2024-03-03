package com.demo.demo.demos.web.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class Consumer {
    private static List<String> queries;
    private static RestTemplate restTemplate;

}
