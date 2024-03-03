package com.demo.demo.demos.web.Controller;

import com.alibaba.fastjson.JSONObject;
import com.demo.demo.demos.web.Service.GetLocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class NumController {
    @Autowired
    private GetLocService getLocService;

    @RequestMapping(value = "/input/",method = RequestMethod.GET,produces = "text/html;charset=UTF-8")
    public String getResponse(@RequestParam(name="number") String number) {
        String res = getLocService.getLoc(number);
        System.out.println("res is " + res);
        return res;
    }
}
