package com.realtimefxrate.validation;

import com.realtimefxrate.dto.RateDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RateValidator {

    public static List<String> validate(RateDTO rate, Validator validator) {
        List<String> errorMessages = new ArrayList<>();

        // Existing validation for null checks
        Set<ConstraintViolation<RateDTO>> violations = validator.validate(rate);
        for (ConstraintViolation<RateDTO> violation : violations) {
            errorMessages.add(violation.getMessage());
        }

        // Custom validation rules
        if (rate.getBid() < 0) {
            errorMessages.add("Bid price must not be negative.");
        }
        if (rate.getAsk() < 0) {
            errorMessages.add("Ask price must not be negative.");
        }
        if (rate.getBid() > rate.getAsk()) {
            errorMessages.add("Bid price must not be greater than ask price.");
        }
        if (!isValidCurrencyPair(rate.getPair())) {
            errorMessages.add("Currency pair format is invalid.");
        }

        long currentTime = System.currentTimeMillis();
        if (rate.getTimestamp() < currentTime - 10000) { // 10 seconds threshold
            errorMessages.add("Timestamp is too old.");
        }

        return errorMessages;
    }

    private static boolean isValidCurrencyPair(String pair) {
        return pair != null && pair.matches("^[A-Z]{3}/[A-Z]{3}$");
    }
}
