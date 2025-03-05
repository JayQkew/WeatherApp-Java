package com.example.myweatherapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class UIManager {
    Activity activity;
    Context context;

    AppCompatActivity appCompatActivity;

    UIManager(Activity activity, Context context, AppCompatActivity appCompatActivity){
        this.activity = activity;
        this.context = context;
        this.appCompatActivity = appCompatActivity;
    }

    public void populateScreen(){
//        getData();
        populateUI();
    }

    private void populateUI(){
        TextView tempText = appCompatActivity.findViewById(R.id.currTemp);
        TextView weatherDescText = appCompatActivity.findViewById(R.id.weatherDesc);
        TextView minTempText = appCompatActivity.findViewById(R.id.minTempText);
        TextView currTempText = appCompatActivity.findViewById(R.id.currTempText);
        TextView maxTempText = appCompatActivity.findViewById(R.id.maxTempText);

    }


}
