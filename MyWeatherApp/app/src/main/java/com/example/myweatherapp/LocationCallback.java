package com.example.myweatherapp;

public interface LocationCallback {
    void onLocationReceived(double latitude, double longitude);
    void onLocationFailed();
}
