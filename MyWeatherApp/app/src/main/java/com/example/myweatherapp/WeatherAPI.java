package com.example.myweatherapp;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import javax.security.auth.login.LoginException;

public class WeatherAPI {
    Context context;
    Activity activity;

    static AppCompatActivity appCompatActivity;

    static JSONObject weatherResponse;
    static JSONObject currentResponse;

    WeatherData[] weatherForecast = new WeatherData[6];

    WeatherAPI(Activity activity, Context context){
        this.context = context;
        this.activity = activity;
    }

    WeatherAPI(Activity activity, Context context, AppCompatActivity appCompatActivity){
        this.context = context;
        this.activity = activity;
        this.appCompatActivity = appCompatActivity;
    }

    public void makeAPICall(double lat, double lon){
        String urlForecast = "https://api.openweathermap.org/data/2.5/forecast?lat="
                + lat
                + "&lon="
                + lon
                + "&appid=b5931244e08cf12bc6a3a80b9f24ffc5"
                + "&units=metric";

        String urlCurrent ="https://api.openweathermap.org/data/2.5/weather?lat="
                + lat
                + "&lon="
                + lon
                + "&appid=b5931244e08cf12bc6a3a80b9f24ffc5"
                + "&units=metric";

        JsonObjectRequest requestForecast = new JsonObjectRequest(Request.Method.GET, urlForecast, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    weatherResponse = response;
                    getDataForcast();
                    Log.i("CHECK_TEMP", "FORECAST RECIEVED :" + weatherForecast.toString());
                } catch (Exception e) {
                    Log.i("CHECK_TEMP", "FAILED TO RESPOND: "+ e.toString());
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("CHECK_TEMP", "ERROR: " + error.toString());
                Log.e("CHECK_TEMP", "LAT: " + lat );
                Log.e("CHECK_TEMP", "LON: " + lon );
            }
        });

        JsonObjectRequest requestCurrent = new JsonObjectRequest(Request.Method.GET, urlCurrent, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    currentResponse = response;
                    getDataCurrent();
                    Log.i("CHECK_TEMP", "FORECAST RECIEVED :" + currentResponse.toString());
                } catch (Exception e) {
                    Log.i("CHECK_TEMP", "FAILED TO RESPOND: "+ e.toString());
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("CHECK_TEMP", "ERROR: " + error.toString());
                Log.e("CHECK_TEMP", "LAT: " + lat );
                Log.e("CHECK_TEMP", "LON: " + lon );
            }
        });

        Volley.newRequestQueue(context).add(requestForecast);
        Volley.newRequestQueue(context).add(requestCurrent);
    }

    private void getDataForcast(){
        Log.i("CHECK_TEMP", "HERE");
        try {
            JSONArray list = weatherResponse.getJSONArray("list");
            Log.i("CHECK_TEMP", "HERE BEFORE FORECAST TEMP");
            for (int i = 1; i < 6; i++){
                JSONObject weatherData = list.getJSONObject(i);
                JSONObject main = weatherData.getJSONObject("main");

                int currTemp = (int)Math.round(main.getDouble("temp"));
                int minTemp = (int)Math.round(main.getDouble("temp_min"));
                int maxTemp = (int)Math.round(main.getDouble("temp_max"));

                JSONArray weather = weatherData.getJSONArray("weather");
                JSONObject weatherObj = weather.getJSONObject(0);
                String condition = weatherObj.getString("main");

                weatherForecast[i] = new WeatherData(condition, currTemp, minTemp, maxTemp);
            }
            Log.i("CHECK_TEMP", "HERE AFTER FORECAST TEMP");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    populateUI();
                }
            });
        } catch (JSONException e) {
            Log.e("CHECK_TEMP_ERROR", e.toString());
        }
    }

    private void getDataCurrent(){
        try {
            JSONObject main = currentResponse.getJSONObject("main");
            JSONObject weather = currentResponse.getJSONArray("weather").getJSONObject(0);

            int currTemp = (int)main.getDouble("temp");
            int minTemp = (int)main.getDouble("temp_min");
            int maxTemp = (int)main.getDouble("temp_max");

            String condition = weather.getString("main");
            Log.i("CHECK_TEMP", "HERE BEFORE CURR TEMP");
            weatherForecast[0] = new WeatherData(condition, currTemp, minTemp, maxTemp);
            Log.i("CHECK_TEMP", "HERE AFTER CURR TEMP");
        } catch (JSONException e){
            Log.e("CHECK_TEMP_ERROR", e.toString());
        }
    }
    private void populateUI(){
        TextView tempText = appCompatActivity.findViewById(R.id.currTemp);
        TextView weatherDescText = appCompatActivity.findViewById(R.id.weatherDesc);
        TextView minTempText = appCompatActivity.findViewById(R.id.minTempText);
        TextView currTempText = appCompatActivity.findViewById(R.id.currTempText);
        TextView maxTempText = appCompatActivity.findViewById(R.id.maxTempText);
        ImageView backgroundView = appCompatActivity.findViewById(R.id.imageView);
        LinearLayout root = appCompatActivity.findViewById(R.id.root);

        tempText.setText(Integer.toString(weatherForecast[0].currTemp) + '°');
        weatherDescText.setText((weatherForecast[0].condition).toUpperCase());
        minTempText.setText(Integer.toString(weatherForecast[0].min) + '°');
        currTempText.setText(Integer.toString(weatherForecast[0].currTemp) + '°');
        maxTempText.setText(Integer.toString(weatherForecast[0].max) + '°');

        String currCondition = weatherForecast[0].condition.toLowerCase();
        int backgroundRes = 0;
        int colorRes = 0;

        switch (currCondition) {
            case "clear":
                backgroundRes = R.drawable.forest_sunny;
                colorRes = R.color.sunny_bg;
                break;
            case "clouds":
            case "mist":
            case "haze":
            case "partlysunny":
                backgroundRes = R.drawable.forest_cloudy;
                colorRes = R.color.cloudy_bg;
                break;
            case "drizzle":
            case "thunderstorm":
            case "rain":
                backgroundRes = R.drawable.forest_rainy;
                colorRes = R.color.rainy_bg;
                break;
            default:
                backgroundRes = R.drawable.forest_sunny; // Set a default icon if no match
                colorRes = R.color.sunny_bg;
                break;
        }

        backgroundView.setImageResource(backgroundRes);
        root.setBackgroundColor(ContextCompat.getColor(appCompatActivity, colorRes));

        String[] days = nextFiveDays();

        for(int i = 1; i < 6; i++){
            int tempID = appCompatActivity.getResources().getIdentifier("temp" + i, "id", appCompatActivity.getPackageName());
            int iconID = appCompatActivity.getResources().getIdentifier("icon" + i, "id", appCompatActivity.getPackageName());
            int dayID = appCompatActivity.getResources().getIdentifier("day" + i, "id", appCompatActivity.getPackageName());
            TextView dayTempText = appCompatActivity.findViewById(tempID);
            ImageView icon = appCompatActivity.findViewById(iconID);
            TextView dayText = appCompatActivity.findViewById(dayID);

            dayText.setText(days[i-1]);
            dayTempText.setText(Integer.toString(weatherForecast[i].currTemp) + '°');
            String condition = weatherForecast[i].condition.toLowerCase();
            int iconResID = 0;

            switch (condition) {
                case "clear":
                    iconResID = R.drawable.clear3x;
                    break;
                case "clouds":
                case "mist":
                case "haze":
                case "partlysunny":
                    iconResID = R.drawable.partlysunny3x;
                    break;
                case "drizzle":
                case "thunderstorm":
                case "rain":
                    iconResID = R.drawable.rain3x;
                    break;
                default:
                    iconResID = R.drawable.clear3x; // Set a default icon if no match
                    break;
            }

            icon.setImageResource(iconResID);

        }
    }

    private String[] nextFiveDays(){
        Locale locale = Locale.getDefault(); // Change to Locale.US if you want English day names
        String[] days = new String[5];
        for (int i = 1; i <= 5; i++) {
            LocalDate date = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                date = LocalDate.now().plusDays(i);
            }
            String dayName = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
            }
            System.out.println(dayName);
            days[i-1] = dayName;
        }

        return days;
    }
}
