package com.example.myweatherapp;

public class WeatherData {
    String condition;
    int currTemp, min, max;

    WeatherData(String condition, int currTemp, int min, int max){
        this.condition = condition;
        this.currTemp = currTemp;
        this.min = min;
        this.max = max;
    }
}
