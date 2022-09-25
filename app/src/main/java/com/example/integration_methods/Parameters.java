package com.example.integration_methods;

/**Ккласс параметров **/
public class Parameters {
    /** Функция **/
    private String function;
    /** Левая граница **/
    private Double leftLimit;
    /** Правая граница **/
    private Double rightLimit;
    /** Кол-во знаков после запятой **/
    private Integer roundNumber;
    /** Точность **/
    private Double accuracy;

    public Parameters(String function, Double leftLimit, Double rightLimit, Integer roundNumber, Double accuracy) {
        this.function = function;
        this.leftLimit = leftLimit;
        this.rightLimit = rightLimit;
        this.roundNumber = roundNumber;
        this.accuracy = accuracy;
    }

    /** Геттер для функции **/
    public String getFunction() {
        return function;
    }

    /** Геттер для левой границы **/
    public Double getLeftLimit() {
        return leftLimit;
    }

    /** Геттер для правой границы **/
    public Double getRightLimit() {
        return rightLimit;
    }

    /** Геттер для кол-ва цифр после запятой **/
    public Integer getRoundNumber() {
        return roundNumber;
    }

    /** Геттер для точности **/
    public Double getAccuracy() {
        return accuracy;
    }
}
