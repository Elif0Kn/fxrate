package com.realtimefxrate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RateDTO {
    @NotBlank(message = "Provider must not be empty")
    private String provider; //Data source

    @NotBlank(message = "Currency pair must not be empty")
    private String pair; //Currency pair

    @NotNull(message = "Bid price must not be null")
    private double bid; //Buying price

    @NotNull(message = "Ask price must not be null")
    private double ask; //Selling price

    @NotNull(message = "Timestamp must not be null")
    private long timestamp; //Time of the rate update

    // Getters & Setters
    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public String getPair() {
        return pair;
    }
    public void setPair(String pair) {
        this.pair = pair;
    }
    
    public double getBid() {
        return bid;
    }
    public void setBid(double bid) {
        this.bid = bid;
    }
    
    public double getAsk() { 
    return ask;
    }
    public void setAsk(double ask) {
        this.ask = ask;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
