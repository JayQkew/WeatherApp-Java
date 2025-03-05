package com.example.myweatherapp;

import org.json.JSONObject;

public interface WeatherDataListener {
    void onWeatherDataReceived(JSONObject weatherData);
}
