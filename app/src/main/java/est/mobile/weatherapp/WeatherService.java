package est.mobile.weatherapp;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("forecast")
    Call<JsonObject> getWeatherData(
            @Query("latitude") String latitude,
            @Query("longitude") String longitude,
            @Query("daily") String daily,
            @Query("current_weather") String currentWeather,
            @Query("timezone") String timezone
    );
}
