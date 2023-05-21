package est.mobile.weatherapp;

import com.google.gson.annotations.SerializedName;

public class WeatherForecast {
    @SerializedName("timestamp")
    private long timestamp;

    @SerializedName("weathercode")
    private int weatherCode;

    @SerializedName("temperature_2m")
    private double temperature;

    @SerializedName("wind_speed_10m")
    private double windSpeed;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(int weatherCode) {
        this.weatherCode = weatherCode;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
}
