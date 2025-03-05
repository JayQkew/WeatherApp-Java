package com.example.myweatherapp;

import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.ActivityChooserView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class UserLocation {
    Activity activity;
    Context context;

    //    private WeatherAPI _weatherAPI;
    public static double longitude;
    public static double latitude;
    FusedLocationProviderClient mFusedLocationClient;
    private final int PERMISSION_ID = 44;
    AppCompatActivity appCompatActivity;

    UserLocation(Activity activity, Context context, AppCompatActivity appCompatActivity) {
        this.activity = activity;
        this.context = context;
        this.appCompatActivity = appCompatActivity;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    /**
     * @return whether the location is enabled or not
     */
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * request for location permissions
     */
    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    /**
     * @return if location permissions have been granted by the device
     */
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        activity.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            Log.d("USER_LOCATION", Double.toString(location.getLatitude()));
                            Log.d("USER_LOCATION", Double.toString(location.getLongitude()));

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            WeatherAPI weatherAPI = new WeatherAPI(activity, context);
                            weatherAPI.makeAPICall(latitude, longitude);
                        }
                    }
                });
            } else {
                Intent intent = new Intent(context, ErrorActivity.class);
                activity.startActivity(intent);
            }
        } else {
            showPermissionExplanationDialog();
        }
    }

    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            if(mLastLocation != null){
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();

                WeatherAPI weatherAPI = new WeatherAPI(activity, context);
                weatherAPI.makeAPICall(latitude, longitude);
            }
        }
    };

    private void showPermissionExplanationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = appCompatActivity.getLayoutInflater();
        View customLayout = inflater.inflate(R.layout.custom_dialog_layout, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();
        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button acceptButton = customLayout.findViewById(R.id.acceptBtn);
        Button denyButton = customLayout.findViewById(R.id.denyBtn);

        // Set click listeners for the buttons
        acceptButton.setOnClickListener(v -> {
            requestLocationPermissions();
            dialog.dismiss(); // Close dialog after action
        });

        denyButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ErrorActivity.class);
            activity.startActivity(intent);
            dialog.dismiss(); // Close dialog after action
        });
    }

}



