package com.realtimefxrate.producer;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class RateProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String[] CURRENCY_PAIRS = {"AUD/USD", "EUR/AUD", "EUR/GBP", "EUR/JPY", "EUR/USD", "EUR/TRY", "GBP/CAD", "GBP/USD", "USD/JPY", "USD/TRY"};
    private ScheduledExecutorService scheduledExecutorService;
    private final Random random = new Random();

    @PostConstruct
    public void init() {
        scheduledExecutorService = Executors.newScheduledThreadPool(5);
        sendRate("{\"provider\":\"LP1\",\"pair\":\"EUR/USD\",\"bid\":1.0845,\"ask\":1.0847,\"timestamp\":1700000000000}");
        startProducingRates();
    }

    private void startProducingRates() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            String rateMessage = createRateMessage();
            sendRate(rateMessage);
            System.out.println("Scheduled task executed..."); // Added this to verify the scheduled task is running
        }, 0, 5, TimeUnit.SECONDS); // Produces rates every 5 seconds
    }

    public void sendRate(String rateMessage) {
        rabbitTemplate.convertAndSend("rate.input.queue", rateMessage);
        System.out.println("Sent rate: " + rateMessage); // Added this to verify the message is being sent
    }

    private String createRateMessage() {
        String pair = CURRENCY_PAIRS[random.nextInt(CURRENCY_PAIRS.length)];
        double bid = 1.0 + (random.nextDouble() * 10);
        double ask = bid + 0.002;
        long timestamp = System.currentTimeMillis();

        return String.format("{\"provider\":\"LP1\",\"pair\":\"%s\",\"bid\":%.4f,\"ask\":%.4f,\"timestamp\":%d}",
                pair, bid, ask, timestamp);
    }

    public void shutdown() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
    }

}
