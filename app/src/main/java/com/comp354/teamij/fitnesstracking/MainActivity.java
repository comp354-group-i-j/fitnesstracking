package com.comp354.teamij.fitnesstracking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.View;


import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.comp354.teamij.fitnesstracking.entities.WeatherKeys;
import com.comp354.teamij.fitnesstracking.entities.WeatherResponse;
import com.comp354.teamij.fitnesstracking.utils.MathUtils;
import com.comp354.teamij.fitnesstracking.utils.Parser;
import com.comp354.teamij.fitnesstracking.utils.WeatherDataFetcher;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    // List View Components
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private static final int startYear = 2018;
    private static final int startMonth = 4;

    public static final List<WeatherResponse> weatherResponseList = new LinkedList<>();

    private DecimalFormat formatter = new DecimalFormat("#0.0");
    
    //go to weather chart
    public void viewWeather(View view) {
        Intent intentViewChart = new Intent(this, WeatherChartsActivity.class);
        startActivity(intentViewChart);
    }
    
    //go to fitness menu with list and charts 
    public void viewFitness(View view) {
        Intent intentViewChart = new Intent(this, FitnessMainMenu.class);
        startActivity(intentViewChart);
    }
    
    //go to prediction chart
    public void viewPrediction(View view) {
        Intent intentViewChart = new Intent(this, PredictionActivity.class);
        startActivity(intentViewChart);
    }

    public void logout(View view) {
        Intent intentViewChart = new Intent(this, LoginActivity.class);
        startActivity(intentViewChart);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView averageTemp, averageWindspeed, hotDay, coldDay;
        averageTemp = findViewById(R.id.averageTemperature);
        averageWindspeed = findViewById(R.id.averageWindspeed);
        hotDay = findViewById(R.id.hottestDay);
        coldDay = findViewById(R.id.coldestDay);

        WeatherDataFetcher weatherDataFetcher = new WeatherDataFetcher(this);

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public synchronized void onResponse(String response) {
                try {
                    List<WeatherResponse> list = Parser.stringToItems(response);
                    weatherResponseList.addAll(list);

                    // sort weather list
                    Collections.sort(weatherResponseList, new Comparator<WeatherResponse>() {
                        public int compare(WeatherResponse w1, WeatherResponse w2) {
                            return w1.getDateTime().compareTo(w2.getDateTime());
                        }
                    });

                    Log.d("main", "entries added to list: " + list.size());
                    Log.d("main", "list size: " + weatherResponseList.size());

                    HashMap<WeatherKeys, Double> summaryValues = MathUtils.getSummaryValues(weatherResponseList);

                    averageTemp.setText(formatter.format(summaryValues.get(WeatherKeys.AVERAGETEMP)));
                    averageWindspeed.setText(formatter.format(summaryValues.get(WeatherKeys.AVERAGEWIND)));
                    hotDay.setText(formatter.format(summaryValues.get(WeatherKeys.HOTDAY)));
                    coldDay.setText(formatter.format(summaryValues.get(WeatherKeys.COLDDAY)));
                } catch (IOException e) {
                    Log.e("main", "ResponseListener", e);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("main", "ResponseListener", error);
            }
        };

        weatherDataFetcher.getWeatherData(2018, 1, listener, errorListener);
    }

}
