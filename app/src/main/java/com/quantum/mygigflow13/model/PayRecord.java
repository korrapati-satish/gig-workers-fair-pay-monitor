package com.quantum.mygigflow13.model;
public class PayRecord {
    private int week_number;
    private String city;
    private String platform;
    private String week_start;
    private double hours_worked;
    private double gross_pay;
    private double tips;
    private double platform_fee;
    private double petrol_price;
    private double petrol_price_idx;
    private double cpi;
    private int holiday_flag;
    private double weather_idx_input;
    private double temperature;
    private double humidity;
    private double rainfall;

    // Getters and Setters

    public int getWeek_number() { return week_number; }
    public void setWeek_number(int week_number) { this.week_number = week_number; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getWeek_start() { return week_start; }
    public void setWeek_start(String week_start) { this.week_start = week_start; }

    public double getHours_worked() { return hours_worked; }
    public void setHours_worked(double hours_worked) { this.hours_worked = hours_worked; }

    public double getGross_pay() { return gross_pay; }
    public void setGross_pay(double gross_pay) { this.gross_pay = gross_pay; }

    public double getTips() { return tips; }
    public void setTips(double tips) { this.tips = tips; }

    public double getPlatform_fee() { return platform_fee; }
    public void setPlatform_fee(double platform_fee) { this.platform_fee = platform_fee; }

    public double getPetrol_price() { return petrol_price; }
    public void setPetrol_price(double petrol_price) { this.petrol_price = petrol_price; }

    public double getPetrol_price_idx() { return petrol_price_idx; }
    public void setPetrol_price_idx(double petrol_price_idx) { this.petrol_price_idx = petrol_price_idx; }

    public double getCpi() { return cpi; }
    public void setCpi(double cpi) { this.cpi = cpi; }

    public int getHoliday_flag() { return holiday_flag; }
    public void setHoliday_flag(int holiday_flag) { this.holiday_flag = holiday_flag; }

    public double getWeather_idx_input() { return weather_idx_input; }
    public void setWeather_idx_input(double weather_idx_input) { this.weather_idx_input = weather_idx_input; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity = humidity; }

    public double getRainfall() { return rainfall; }
    public void setRainfall(double rainfall) { this.rainfall = rainfall; }

    @Override
    public String toString() {
        return "PayRecord{" +
                "week_number=" + week_number +
                ", city='" + city + '\'' +
                ", platform='" + platform + '\'' +
                ", week_start='" + week_start + '\'' +
                ", hours_worked=" + hours_worked +
                ", gross_pay=" + gross_pay +
                ", tips=" + tips +
                ", platform_fee=" + platform_fee +
                ", petrol_price=" + petrol_price +
                ", petrol_price_idx=" + petrol_price_idx +
                ", cpi=" + cpi +
                ", holiday_flag=" + holiday_flag +
                ", weather_idx_input=" + weather_idx_input +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", rainfall=" + rainfall +
                '}';
    }
}

