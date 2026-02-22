package com.realtimefxrate.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue rateInputQueue() {
        return new Queue("rate.input.queue", true);
    }
}
