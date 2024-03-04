package com.backend.backend.demos.web.consumer;

import com.backend.backend.demos.web.consumer.utils.JwtAuthentication;
import com.backend.backend.demos.web.mapper.UserMapper;
import com.backend.backend.demos.web.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    final public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    private User user;
    private Session session = null;

    public static UserMapper userMapper;
    public static RestTemplate restTemplate;
    public static RedisTemplate<String,String> redisTemplate;
    private final static String addRequestUrl = "http://127.0.0.1:3001/request/add/";
    private final static String removePlayerurl = "http://127.0.0.1:3001/player/remove/";

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }
    @Autowired
    public void setRedisTemplate(RedisTemplate<String,String> redisTemplate) {WebSocketServer.redisTemplate = redisTemplate;}
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        this.session = session;
        System.out.println("connected!");
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);
        System.out.println("Received ");
        if (this.user != null) {
            users.put(userId, this);
        } else {
            this.session.close();
        }
        System.out.println(users);
    }

    public String getNum(String key) {
        return redisTemplate.boundValueOps(key).get();
    }
    public boolean hasNum(String key) {
        if(Boolean.TRUE.equals(redisTemplate.hasKey(key))) return true;
        return false;
    }
    @OnClose
    public void onClose() {
        System.out.println("disconnected!");
        if (this.user != null) {
            users.remove(this.user.getId());
        }
    }

    public static String send2client(Integer userId, String location) {
        redisTemplate.boundValueOps(userId.toString()).set(location, 5, TimeUnit.MINUTES);
        if(users.get(userId) != null) {
            users.get(userId).sendMessage(location);
            return "success";
        }
        return "user empty";
    }

    private void startMatching(String location) {
        System.out.println("send to information end!");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("userId", this.user.getId().toString());
        data.add("location",location);
        restTemplate.postForObject(addRequestUrl, data, String.class);
    }

    private void stopMatching() {
        System.out.println("stop matching");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        restTemplate.postForObject(removePlayerurl, data, String.class);
    }

    @OnMessage
    public void onMessage(String message, Session session) {  // 当做路由
        System.out.println("receive message!");
        System.out.println(message);
        String location = message;
        if(hasNum(location)) sendMessage(getNum(location));
        startMatching(location);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) {
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}