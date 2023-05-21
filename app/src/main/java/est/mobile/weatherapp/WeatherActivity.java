package est.mobile.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class WeatherActivity extends AppCompatActivity {
    private static final String TAG = WeatherActivity.class.getSimpleName();

    private TextView currentWeatherText;
    private ImageView currentWeatherIcon;
    private TextView temperatureText;
    private TextView windSpeedText;
    private TextView coordinatesText;
    private LinearLayout forecastLayout;
    private Button volleyButton;
    private Button retrofitButton;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        currentWeatherText = findViewById(R.id.current_weather_text);
        currentWeatherIcon = findViewById(R.id.current_weather_icon);
        temperatureText = findViewById(R.id.temperature_text);
        windSpeedText = findViewById(R.id.wind_speed_text);
        coordinatesText = findViewById(R.id.coordinates_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        volleyButton = findViewById(R.id.volley_button);
        retrofitButton = findViewById(R.id.retrofit_button);

        requestQueue = Volley.newRequestQueue(this);

        volleyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestWeatherDataWithVolley();
            }
        });

        retrofitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tambahkan kode untuk request data cuaca dengan Retrofit di sini
            }
        });
    }

    private void requestWeatherDataWithVolley() {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=-7.98&longitude=112.63&daily=weathercode&current_weather=true&timezone=auto";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "API Response: " + response.toString());

                            // Parse current weather data
                            JSONObject currentWeatherObject = response.getJSONObject("current_weather");
                            WeatherData currentWeather = parseWeatherData(currentWeatherObject);

                            Log.d(TAG, "Parsed Current Weather: " + currentWeather.toString());

                            // Parse forecast data
                            if (response.has("forecast")) {
                                JSONArray forecastArray = response.getJSONArray("forecast");
                                WeatherForecast[] forecast = parseWeatherForecast(forecastArray);
                                Log.d(TAG, "Parsed Forecast: " + Arrays.toString(forecast));
                                // Display forecast data
                                displayForecast(forecast);
                            }

                            // Display current weather data
                            displayCurrentWeather(currentWeather);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley Error: " + error.getMessage());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }


    private WeatherData parseWeatherData(JSONObject jsonObject) throws JSONException {
        WeatherData weatherData = new WeatherData();

        if (jsonObject.has("latitude")) {
            weatherData.setLatitude(jsonObject.getDouble("latitude"));
        }

        if (jsonObject.has("longitude")) {
            weatherData.setLongitude(jsonObject.getDouble("longitude"));
        }

        if (jsonObject.has("temperature")) {
            weatherData.setTemperature(jsonObject.getDouble("temperature"));
        }

        if (jsonObject.has("windspeed")) {
            weatherData.setWindSpeed(jsonObject.getDouble("windspeed"));
        }

        if (jsonObject.has("weathercode")) {
            weatherData.setWeatherCode(jsonObject.getInt("weathercode"));
        }

        return weatherData;
    }



    private WeatherForecast[] parseWeatherForecast(JSONArray jsonArray) throws JSONException {
        WeatherForecast[] forecast = new WeatherForecast[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject forecastObject = jsonArray.getJSONObject(i);
            WeatherForecast weatherForecast = new WeatherForecast();
            if (forecastObject.has("weathercode")) {
                weatherForecast.setWeatherCode(forecastObject.getInt("weathercode"));
            }
            if (forecastObject.has("timestamp")) {
                weatherForecast.setTimestamp(forecastObject.getLong("timestamp"));
            }
            if (forecastObject.has("temperature_2m")) {
                weatherForecast.setTemperature(forecastObject.getDouble("temperature_2m"));
            }
            if (forecastObject.has("wind_speed_10m")) {
                weatherForecast.setWindSpeed(forecastObject.getDouble("wind_speed_10m"));
            }
            forecast[i] = weatherForecast;
        }
        return forecast;
    }



    private void displayCurrentWeather(WeatherData weatherData) {
        String weatherDescription = getWeatherDescription(weatherData.getWeatherCode());
        currentWeatherText.setText(weatherDescription);

        Drawable weatherIcon = getWeatherIcon(weatherData.getWeatherCode());
        currentWeatherIcon.setImageDrawable(weatherIcon);

        String temperature = weatherData.getTemperature() != 0.0
                ? getString(R.string.temperature, String.valueOf(weatherData.getTemperature()))
                : "";
        temperatureText.setText(temperature);

        String windSpeed = weatherData.getWindSpeed() != 0.0
                ? getString(R.string.wind_speed, String.valueOf(weatherData.getWindSpeed()))
                : "";
        windSpeedText.setText(windSpeed);

        String latitude = weatherData.getLatitude() != 0.0
                ? getString(R.string.latitude, String.valueOf(weatherData.getLatitude()))
                : "";
        String longitude = weatherData.getLongitude() != 0.0
                ? getString(R.string.longitude, String.valueOf(weatherData.getLongitude()))
                : "";
        String coordinates = getString(R.string.coordinates, latitude, longitude);
        coordinatesText.setText(coordinates);
    }


    private void displayForecast(WeatherForecast[] forecast) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        forecastLayout.removeAllViews(); // Hapus tampilan sebelumnya

        for (WeatherForecast weatherForecast : forecast) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 16, 0, 16);

            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(layoutParams);
            imageView.setImageDrawable(getWeatherIcon(weatherForecast.getWeatherCode()));
            forecastLayout.addView(imageView);

            TextView textView = new TextView(this);
            textView.setLayoutParams(layoutParams);
            textView.setText(dateFormat.format(new Date(weatherForecast.getTimestamp() * 1000)));
            forecastLayout.addView(textView);

            TextView temperatureView = new TextView(this);
            temperatureView.setLayoutParams(layoutParams);
            temperatureView.setText(getString(R.string.temperature, String.valueOf(weatherForecast.getTemperature())));
            forecastLayout.addView(temperatureView);

            TextView windSpeedView = new TextView(this);
            windSpeedView.setLayoutParams(layoutParams);
            windSpeedView.setText(getString(R.string.wind_speed, String.valueOf(weatherForecast.getWindSpeed())));
            forecastLayout.addView(windSpeedView);
        }
    }


    private String getWeatherDescription(int weatherCode) {
        String description;
        switch (weatherCode) {
            case 0:
                description = "Sunny";
                break;
            case 1:
                description = "Cloudy";
                break;
            case 2:
                description = "Rainy";
                break;
            case 3:
                description = "Stormy";
                break;
            default:
                description = "Unknown";
                break;
        }
        return description;
    }

    private Drawable getWeatherIcon(int weatherCode) {
        Drawable icon;
        switch (weatherCode) {
            case 0:
                icon = getResources().getDrawable(R.drawable.ic_sunny);
                break;
            case 1:
                icon = getResources().getDrawable(R.drawable.ic_cloudy);
                break;
            case 2:
                icon = getResources().getDrawable(R.drawable.ic_rainy);
                break;
            case 3:
                icon = getResources().getDrawable(R.drawable.ic_stormy);
                break;
            default:
                icon = getResources().getDrawable(R.drawable.ic_sunny);
                break;
        }
        return icon;
    }
    
}
