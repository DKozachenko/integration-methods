package com.example.integration_methods;

public class Parameters {
    private String function;
    private Double leftLimit;
    private Double rightLimit;
    private Integer roundNumber;
    private Double accuracy;

    public Parameters(String function, Double leftLimit, Double rightLimit, Integer roundNumber, Double accuracy) {
        this.function = function;
        this.leftLimit = leftLimit;
        this.rightLimit = rightLimit;
        this.roundNumber = roundNumber;
        this.accuracy = accuracy;
    }

    public String getFunction() {
        return function;
    }

    public Double getLeftLimit() {
        return leftLimit;
    }

    public Double getRightLimit() {
        return rightLimit;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public Double getAccuracy() {
        return accuracy;
    }
}
