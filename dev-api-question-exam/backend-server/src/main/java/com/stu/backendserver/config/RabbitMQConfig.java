package com.stu.backendserver.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXAM_QUEUE = "exam.queue";

    @Bean
    public Queue examQueue() {
        return new Queue(EXAM_QUEUE, true);
    }
}