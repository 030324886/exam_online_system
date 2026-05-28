package com.stu.backendserver.controller;

import com.stu.backendserver.config.RabbitMQConfig;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/test")
public class ConcurrencyTestController {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/redis")
    public String redisTest() {
        redisTemplate.opsForValue().set("user:1:info", "在线考试系统用户", 30, TimeUnit.MINUTES);
        return "Redis 缓存成功：" + redisTemplate.opsForValue().get("user:1:info");
    }

    @GetMapping("/lock")
    public String lockTest() {
        RLock lock = redissonClient.getLock("exam:lock:submit");
        try {
            boolean locked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!locked) {
                return "请求频繁，请稍后再试！";
            }
            return "获取分布式锁成功 → 安全执行业务";
        } catch (Exception e) {
            return "出错：" + e.getMessage();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @GetMapping("/mq")
    public String mqTest() {
        String msg = "考试提交异步记录：" + UUID.randomUUID();
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXAM_QUEUE, msg);
        return "消息已发送到 RabbitMQ → " + msg;
    }
}