package com.example.myweatherapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity{
    UserLocation _userLocation;
    UIManager _uiManager;

    WeatherAPI _weatherAPI;
    double longitude;
    double latitude;
    double currTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _userLocation = new UserLocation(this, this, this);
        _weatherAPI =  new WeatherAPI(this, this, this);

        _userLocation.getLastLocation();
    }

    /**
     * keep this here so that UserLocation can call this method
     * @param requestCode The request code passed in
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link PackageManager#PERMISSION_GRANTED}
     *     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Function to get day names dynamically
    private String getDayName(int index) {
        String[] days = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday"};
        return days[index % days.length]; // Just an example
    }

}
