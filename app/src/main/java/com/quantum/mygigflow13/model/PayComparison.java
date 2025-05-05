package com.quantum.mygigflow13.model;

public class PayComparison {
    private String weekStart;
    private String city;
    private String platform;
    private double currentGrossPay;
    private double expectedGrossPay;

    public PayComparison(String weekStart, String city, String platform,
                         double currentGrossPay, double expectedGrossPay) {
        this.weekStart = weekStart;
        this.city = city;
        this.platform = platform;
        this.currentGrossPay = currentGrossPay;
        this.expectedGrossPay = expectedGrossPay;
    }

    public String getWeekStart() {
        return weekStart;
    }

    public String getCity() {
        return city;
    }

    public String getPlatform() {
        return platform;
    }

    public double getCurrentGrossPay() {
        return currentGrossPay;
    }

    public double getExpectedGrossPay() {
        return expectedGrossPay;
    }

    @Override
    public String toString() {
        return "PayComparison{" +
                "weekStart='" + weekStart + '\'' +
                ", city='" + city + '\'' +
                ", platform='" + platform + '\'' +
                ", currentGrossPay=" + currentGrossPay +
                ", expectedGrossPay=" + expectedGrossPay +
                '}';
    }
}
