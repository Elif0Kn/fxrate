package com.realtimefxrate.dto;

public class AlertMessageDTO {
    private String pair;
    private double spread;

    public AlertMessageDTO(String pair, double spread) {
        this.pair = pair;
        this.spread = spread;
    }

    // Getters and Setters
    public String getPair() {
        return pair;
    }

    public void setPair(String pair) {
        this.pair = pair;
    }

    public double getSpread() {
        return spread;
    }

    public void setSpread(double spread) {
        this.spread = spread;
    }
}
