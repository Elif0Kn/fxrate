package com.realtimefxrate.service;

import com.realtimefxrate.dto.RateDTO;
import com.realtimefxrate.dto.AlertMessageDTO;
import com.realtimefxrate.web.RateWebSocketHandler;
import com.realtimefxrate.validation.RateValidator;
import com.realtimefxrate.cexception.CustomValidationException;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Validator;

@Service
public class RateService {
    private IMap<String, RateDTO> ratesCache;
    private static final double ALERT_THRESHOLD = 0.02; // Randomly decided threshold for spread alerting

    @Autowired
    private Validator validator;

    @Autowired
    private RateWebSocketHandler rateWebSocketHandler;

    @Autowired
    public RateService(HazelcastInstance hazelcastInstance) {
        this.ratesCache = hazelcastInstance.getMap("rates");
    }

    
    public List<String> validateRate(RateDTO rate) {
        return RateValidator.validate(rate, validator);
    }

    public void processRate(RateDTO rate) throws JsonProcessingException {
        List<String> validationErrors = validateRate(rate);
        if (!validationErrors.isEmpty()) {
            throw new CustomValidationException(String.join(", ", validationErrors));
        }
        ratesCache.put(rate.getPair(), rate); // This is thread-safe and will automatically handle TTL based on Hazelcast configuration
        
        // Calculate spread
        double spread = calculateSpread(rate.getBid(), rate.getAsk());
        if (spread > ALERT_THRESHOLD) {
            // Trigger alert if the spread is above the threshold
            sendAlert(rate.getPair(), spread);
        }
        // Send real-time update to WebSocket clients
        String rateJson = new ObjectMapper().writeValueAsString(rate);
        rateWebSocketHandler.sendRateUpdate(rateJson, rate.getPair());
    }

    // Spread calculation
    private double calculateSpread(double bid, double ask) {
        return ask - bid;
    }

    private void sendAlert(String pair, double spread) throws JsonProcessingException {
        // Create an alert message object
        AlertMessageDTO alertMessage = new AlertMessageDTO(pair, spread);
    
        // Convert the alert message to JSON
        String alertJson = new ObjectMapper().writeValueAsString(alertMessage);
    
        // Send the alert through WebSocket
        rateWebSocketHandler.sendAlertUpdate(alertJson);

        // For simplicity, also log the alert
        System.out.println("Alert: High spread for " + pair + " - Spread: " + spread);
    }


    public List<RateDTO> getAllRates() {
        return new ArrayList<>(ratesCache.values());
    }

    public RateDTO getRateByPair(String pair) {
        return ratesCache.get(pair);
    }


    public List<RateDTO> getRatesByPairs(String[] pairs) {
        List<RateDTO> rates = new ArrayList<>();
        for (String pair : pairs) {
            RateDTO rate = ratesCache.get(pair);
            if (rate != null) {
                rates.add(rate);
            }
        }
        return rates;
    }

    // Method to remove a rate from the cache, currently not used or needed but can be useful for cleanup of the cache
    public void removeRate(String pair) {
        ratesCache.remove(pair);
    }

}