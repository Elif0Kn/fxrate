package com.realtimefxrate.controller;

import com.realtimefxrate.dto.RateDTO;
import com.realtimefxrate.service.RateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/rates")
public class RateController {

    @Autowired
    private RateService rateService;

    /*
     * Get all rates
     * return list of all FX rates
     */
    @GetMapping
    public List<RateDTO> getAllRates() {
        return rateService.getAllRates();
    }

    /*
     * Get the latest rate for a specific currency pair
     * param:pair - Currency pair (e.g., "EUR/USD")
     * return latest rate for the specified pair
     */
    @GetMapping("/{pair}")
    public RateDTO getRateByPair(@PathVariable String pair) {
        RateDTO rate = rateService.getRateByPair(pair);
        if (rate == null) {
            throw new RuntimeException("Rate not found for pair: " + pair);
        }
        return rate;
    }

    /*
     * Get the latest rates for multiple currency pairs
     * param:pairs - comma-separated currency pairs (e.g., "EUR/USD,USD/JPY")
     * return list of latest rates for the specified pairs
     */
    @GetMapping("/multiple")
    public List<RateDTO> getRatesByPairs(@RequestParam String pairs) {
        String[] pairArray = pairs.split(",");
        List<RateDTO> rates = new ArrayList<>();

        // Retrieve rates for each pair
        for (String pair : pairArray) {
            RateDTO rate = rateService.getRateByPair(pair);
            if (rate != null) {
                rates.add(rate);
            }
        }
        return rates;
    }

    
    @PostMapping
    public ResponseEntity<?> addRate(@Valid @RequestBody RateDTO rate) {
        try {
            rateService.processRate(rate);
            return ResponseEntity.ok(rate);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Error processing rate: " + e.getMessage());
        }
    }
}
