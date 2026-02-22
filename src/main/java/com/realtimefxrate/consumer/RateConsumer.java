package com.realtimefxrate.consumer;

import com.realtimefxrate.dto.RateDTO;
import com.realtimefxrate.service.RateService;
import com.realtimefxrate.cexception.CustomValidationException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class RateConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RateConsumer.class);

    @Autowired
    private RateService rateService;

    @RabbitListener(queues = "rate.input.queue")
    public void receiveRateMessage(String rateMessage) {
        try {
            RateDTO rate = new ObjectMapper().readValue(rateMessage, RateDTO.class);
            List<String> validationErrors = rateService.validateRate(rate);
            
            if (!validationErrors.isEmpty()) {
                throw new CustomValidationException("Validation failed: " + String.join(", ", validationErrors));
            }

            // Check timestamp for ignoring old messages
            if (isOldMessage(rate)) {
                logger.warn("Ignoring old rate message: {}", rate);
                return;
            }
            rateService.processRate(rate);
            
        // Log and handle all kinds of exceptions (generic catch for simplicity, can be refined to specific exceptions)
        } catch (Exception e) {
            logger.error("Error processing rate message: {}", rateMessage, e);
        }
    }

    private boolean isOldMessage(RateDTO rate) {
        long currentTime = System.currentTimeMillis();
        long messageTimestamp = rate.getTimestamp();
        
        return (currentTime - messageTimestamp) > 10000; // Assuming we want to ignore messages older than 10 seconds
    }

}
