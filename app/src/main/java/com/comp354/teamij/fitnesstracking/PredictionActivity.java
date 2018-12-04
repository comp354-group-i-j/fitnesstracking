package com.comp354.teamij.fitnesstracking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.comp354.teamij.fitnesstracking.utils.LinearEquationSolver;
import com.comp354.teamij.fitnesstracking.utils.WorkoutDataFetcher;


import com.comp354.teamij.fitnesstracking.utils.MovingAverage;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.moomeen.endo2java.model.Workout;
import com.comp354.teamij.fitnesstracking.entities.WeatherResponse;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.Math;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;

public class PredictionActivity extends AppCompatActivity {

    static WorkoutDataFetcher workoutDataFetcher = new WorkoutDataFetcher();
    static List<Workout> workouts = workoutDataFetcher.getWorkouts();

    static List<WeatherFitnessData> weatherFitnessData = new ArrayList<WeatherFitnessData>();

    static LineGraphSeries<DataPoint> distance = new LineGraphSeries<DataPoint>();
    static LineGraphSeries<DataPoint> daysOff = new LineGraphSeries<DataPoint>();
    static LineGraphSeries<DataPoint> duration = new LineGraphSeries<DataPoint>();
    static LineGraphSeries<DataPoint> avgSpeed = new LineGraphSeries<DataPoint>();
    static LineGraphSeries<DataPoint> temperature = new LineGraphSeries<DataPoint>();
    static LineGraphSeries<DataPoint> windSpeed = new LineGraphSeries<DataPoint>();
    static LineGraphSeries<DataPoint> MA10 = new LineGraphSeries<DataPoint>();
    static LineGraphSeries<DataPoint> MA20 = new LineGraphSeries<DataPoint>();
    static GraphView graph;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        Button allButton = findViewById(R.id.AllButton);
        Button  durationButton = findViewById(R.id.DurationButton);
        Button distanceButton = findViewById(R.id.DistanceButton);
        Button speedButton = findViewById(R.id.SpeedButton);

        Date day0 = workouts.get(workouts.size() - 1).getStartTime().toDate();     // First day
        Date dayN = workouts.get(0).getStartTime().toDate();     // Last day
        mergeFitnessWeatherData(day0, dayN);

        graph = (GraphView) findViewById(R.id.PredictionGraph);

        graph.removeAllSeries();
        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(day0.getTime());
        graph.getViewport().setMaxX(dayN.getTime());
        graph.getViewport().setXAxisBoundsManual(true);






        // Calculate number of days off prior
        int daysOffCount;
        for (int i = 1; i < weatherFitnessData.size(); i++) {
            daysOffCount = 0;
            for (int j = i - 1; j >= 0; j--) {
                if (weatherFitnessData.get(j).duration == 0)
                    daysOffCount++;
                if (weatherFitnessData.get(j).duration > 1)
                    break;
            }
            weatherFitnessData.get(i).daysOff = daysOffCount;
        }










        allButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        generateGraph(0);
                        //view
                    }
                }
        );

        durationButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                        generateGraph(1);

                        TextView predictionView = findViewById(R.id.PredictionView);

                        double durationBest = 0;
                        double distanceBest = 0;
                        double speedBest = 0;

                        for (int i = 0; i < workouts.size(); i++) {
                            if (workouts.get(i).getDuration().getStandardMinutes() + 0.0 > durationBest)
                                durationBest = workouts.get(i).getDuration().getStandardMinutes() + 0.0;
                        }
                        for (int i = 0; i < workouts.size(); i++) {
                            if (workouts.get(i).getDistance() > distanceBest)
                                distanceBest = workouts.get(i).getDistance();
                        }
                        for (int i = 0; i < workouts.size(); i++) {
                            if (workouts.get(i).getSpeedAvg() > speedBest)
                                speedBest = workouts.get(i).getSpeedAvg();
                        }

                        // 80% historical data
                        int historicalData = (int) (weatherFitnessData.size() * 0.8);

                        // Trim to modulo 3 = 0
                        while (historicalData % 3 != 0)
                            historicalData--;

                        float[] durationCoeffs = LinearEquationSolver.calcCoefficients(historicalData, 0, weatherFitnessData);

                        float testWind = weatherFitnessData.get(weatherFitnessData.size() - 1).windSpeed;
                        float testTemp = weatherFitnessData.get(weatherFitnessData.size() - 1).temperature;
                        int testRest = Math.min(weatherFitnessData.get(weatherFitnessData.size() - 1).daysOff, 5);

                        float newDuration = durationCoeffs[0] * testWind + durationCoeffs[1] * testTemp + durationCoeffs[2] * testRest;

                        predictWorkout(newDuration, durationBest, 0, predictionView);
                    }
                }

        );

        distanceButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        generateGraph(2);

                        TextView predictionView = findViewById(R.id.PredictionView);

                        double durationBest = 0;
                        double distanceBest = 0;
                        double speedBest = 0;

                        for (int i = 0; i < workouts.size(); i++) {
                            if (workouts.get(i).getDuration().getStandardMinutes() + 0.0 > durationBest)
                                durationBest = workouts.get(i).getDuration().getStandardMinutes() + 0.0;
                        }
                        for (int i = 0; i < workouts.size(); i++) {
                            if (workouts.get(i).getDistance() > distanceBest)
                                distanceBest = workouts.get(i).getDistance();
                        }
                        for (int i = 0; i < workouts.size(); i++) {
                            if (workouts.get(i).getSpeedAvg() > speedBest)
                                speedBest = workouts.get(i).getSpeedAvg();
                        }

                        // 80% historical data
                        int historicalData = (int) (weatherFitnessData.size() * 0.8);

                        // Trim to modulo 3 = 0
                        while (historicalData % 3 != 0)
                            historicalData--;

                        float[] distanceCoeffs = LinearEquationSolver.calcCoefficients(historicalData, 1, weatherFitnessData);

                        float testWind = weatherFitnessData.get(weatherFitnessData.size() - 1).windSpeed;
                        float testTemp = weatherFitnessData.get(weatherFitnessData.size() - 1).temperature;
                        int testRest = Math.min(weatherFitnessData.get(weatherFitnessData.size() - 1).daysOff, 5);

                        float newDistance = distanceCoeffs[0] * testWind + distanceCoeffs[1] * testTemp + distanceCoeffs[2] * testRest;
                        predictWorkout(newDistance, distanceBest, 1, predictionView);


                    }
                }
                // view
        );

        speedButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        generateGraph(3);

                        TextView predictionView = findViewById(R.id.PredictionView);

                        double durationBest = 0;
                        double distanceBest = 0;
                        double speedBest = 0;

                        for (int i = 0; i < workouts.size(); i++) {
                            if (workouts.get(i).getDuration().getStandardMinutes() + 0.0 > durationBest)
                                durationBest = workouts.get(i).getDuration().getStandardMinutes() + 0.0;
                        }
                        for (int i = 0; i < workouts.size(); i++) {
                            if (workouts.get(i).getDistance() > distanceBest)
                                distanceBest = workouts.get(i).getDistance();
                        }
                        for (int i = 0; i < workouts.size(); i++) {
                            if (workouts.get(i).getSpeedAvg() > speedBest)
                                speedBest = workouts.get(i).getSpeedAvg();
                        }

                        // 80% historical data
                        int historicalData = (int) (weatherFitnessData.size() * 0.8);

                        // Trim to modulo 3 = 0
                        while (historicalData % 3 != 0)
                            historicalData--;

                        float[] speedCoeffs = LinearEquationSolver.calcCoefficients(historicalData, 2, weatherFitnessData);

                        float testWind = weatherFitnessData.get(weatherFitnessData.size() - 1).windSpeed;
                        float testTemp = weatherFitnessData.get(weatherFitnessData.size() - 1).temperature;
                        int testRest = Math.min(weatherFitnessData.get(weatherFitnessData.size() - 1).daysOff, 5);

                        float newSpeed = speedCoeffs[0] * testWind + speedCoeffs[1] * testTemp + speedCoeffs[2] * testRest;
                        predictWorkout(newSpeed, speedBest, 2, predictionView);
                    }
                }
        );
    }


    public class WeatherFitnessData {
        public Date date;
        public double distance;
        public double duration;
        public double avgSpeed;
        public int daysOff;
        public float temperature;
        public float windSpeed;
        public double durationMA10;
        public double durationMA20;
        public double distanceMA10;
        public double distanceMA20;
        public double speedMA10;
        public double speedMA20;

        WeatherResponse weatherResponse;

        WeatherFitnessData(Date date, double distance, double duration, double avgSpeed, float temperature, float windSpeed,
                           double durationMA10, double durationMA20,
                           double distanceMA10, double distanceMA20,
                           double speedMA10, double speedMA20) {
            this.date = date;
            this.distance = distance;
            this.duration = duration;
            this.avgSpeed = avgSpeed;
            this.temperature = temperature;
            this.windSpeed = windSpeed;
            this.daysOff = 0;
            this.durationMA10 = durationMA10;
            this.durationMA20 = durationMA20;
            this.distanceMA10 = distanceMA10;
            this.distanceMA20 = distanceMA20;
            this.speedMA10 = speedMA10;
            this.speedMA20 = speedMA20;
        }
    }

    public void mergeFitnessWeatherData(Date day0, Date dayN) {
        // Iterate through start and end dates.
        // Padding with off-days.

        // Calculate date of first workout, and of last, and total number of days.
        int totalDays = getDifferenceDays(day0, dayN);
        Calendar start = Calendar.getInstance();
        start.setTime(day0);
        Calendar end = Calendar.getInstance();
        end.setTime(dayN);

        MovingAverage durationMA10 = new MovingAverage(10);
        MovingAverage durationMA20 = new MovingAverage(20);
        MovingAverage distanceMA10 = new MovingAverage(10);
        MovingAverage distanceMA20 = new MovingAverage(20);
        MovingAverage speedMA10 = new MovingAverage(10);
        MovingAverage speedMA20 = new MovingAverage(20);

        for (Date date = start.getTime(); !start.after(end); start.add(Calendar.DATE, 1), date = start.getTime()) {

            int workoutIndex = -1;
            int weatherIndex = -1;

            for (int i = 0; i < workouts.size(); i++) {
                if (workouts.get(i).getStartTime().toDate().getYear() == date.getYear()
                        && workouts.get(i).getStartTime().toDate().getMonth() == date.getMonth()
                        && workouts.get(i).getStartTime().toDate().getDay() == date.getDay()) {
                    workoutIndex = i;
                    break;
                }
            }

            for (int i = 0; i < MainActivity.weatherResponseList.size(); i++) {
                if (MainActivity.weatherResponseList.get(i).getDateTime().getYear() == date.getYear()
                        && MainActivity.weatherResponseList.get(i).getDateTime().getMonth() == date.getMonth()
                        && MainActivity.weatherResponseList.get(i).getDateTime().getDay() == date.getDay()) {
                    weatherIndex = i;
                    break;
                }
            }

            if (workoutIndex != -1 && weatherIndex != -1) {

                double y = workouts.get(workoutIndex).getDistance();
                double z = workouts.get(workoutIndex).getDuration().getStandardMinutes() + 0.0;
                double r = workouts.get(workoutIndex).getSpeedAvg();
                float w = MainActivity.weatherResponseList.get(weatherIndex).getTemperature();
                float q = MainActivity.weatherResponseList.get(weatherIndex).getWindSpeed();

                distanceMA10.addData(y);
                distanceMA10.getMean();
                distanceMA20.addData(y);
                distanceMA20.getMean();

                durationMA10.addData(z);
                durationMA10.getMean();
                durationMA20.addData(z);
                durationMA20.getMean();

                speedMA10.addData(r);
                speedMA10.getMean();
                speedMA20.addData(r);
                speedMA20.getMean();

                weatherFitnessData.add(new WeatherFitnessData(date, y, z, r, w, q,
                        durationMA10.getMean(), durationMA20.getMean(),
                        distanceMA10.getMean(), distanceMA20.getMean(),
                        speedMA10.getMean(), speedMA20.getMean()));


            } else {
                Float w = MainActivity.weatherResponseList.get(weatherIndex).getTemperature();
                Float q = MainActivity.weatherResponseList.get(weatherIndex).getWindSpeed();


                distanceMA10.addData(0.0);
                distanceMA10.getMean();
                distanceMA20.addData(0.0);
                distanceMA20.getMean();

                durationMA10.addData(0.0);
                durationMA10.getMean();
                durationMA20.addData(0.0);
                durationMA20.getMean();

                speedMA10.addData(0.0);
                speedMA10.getMean();
                speedMA20.addData(0.0);
                speedMA20.getMean();

                weatherFitnessData.add(new WeatherFitnessData(date, 0.0, 0.0, 0.0, w, q,
                        durationMA10.getMean(), durationMA20.getMean(),
                        distanceMA10.getMean(), distanceMA20.getMean(),
                        speedMA10.getMean(), speedMA20.getMean()));
            }

        }
    }

    public static int getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public void predictWorkout(float metric, double best, int selection, TextView output) {

    double percentDiff = (best - metric) / best;

        switch (selection) {
            case 0:

    if (percentDiff > 0.15)
        output.setText(String.format("Very unfavorable conditions. Projected duration is %.0f minutes, %.2f%% chance to get new best", metric, (1.0/8 - percentDiff ) * 100));
    else if (percentDiff <= 0.15 && percentDiff >= 0.10) {
        if (weatherFitnessData.get(
                weatherFitnessData.size() - 1).durationMA10 > weatherFitnessData.get(weatherFitnessData.size() - 1).durationMA20)
            output.setText(String.format("Neutral conditions. Upward trend. Projected duration is %.0f minutes, %.2f%% chance to get new best", metric, (3.0/8 - percentDiff ) * 100));

        else
            output.setText(String.format("Neutral conditions. Downward trend. Projected duration is %.0f minutes, %.2f%% chance to get new best", metric, (2.0/8 - percentDiff ) * 100));
    } else if (percentDiff < 0.10 && percentDiff >= 0.05) {
        if (weatherFitnessData.get(
                weatherFitnessData.size() - 1).durationMA10 > weatherFitnessData.get(weatherFitnessData.size() - 1).durationMA20)
            output.setText(String.format("Favorable conditions. Upward trend. Projected duration is %.0f minutes, %.2f%% chance to get new best", metric, (4.0/8 - percentDiff ) * 100));

        else
            output.setText(String.format("Favorable conditions, but downward trend. Projected duration is %.0f minutes, %.2f%% chance to get new best", metric, (3.0/8 - percentDiff ) * 100));

    } else if (percentDiff < 0.05 && percentDiff >= 0.0) {
        if (weatherFitnessData.get(
                weatherFitnessData.size() - 1).durationMA10 > weatherFitnessData.get(weatherFitnessData.size() - 1).durationMA20)
            output.setText(String.format("Very favorable conditions. Upward trend. Projected duration is %.0f minutes, %.2f%% chance to get new best", metric, (6.0/8 - percentDiff ) * 100));

        else
            output.setText(String.format("Very favorable conditions, but downward trend. Projected duration is %.0f minutes, %.2f%% chance to get new best", metric, (5.0/8 - percentDiff ) * 100));

    } else if (metric - best > 0) {
        if (weatherFitnessData.get(
                weatherFitnessData.size() - 1).durationMA10 > weatherFitnessData.get(weatherFitnessData.size() - 1).durationMA20)
            output.setText(String.format("Perfect conditions. Upward trend. Projected duration is %.0f minutes, %.2f%% chance to get new best", metric, (8.0/8 - percentDiff ) * 100));
        else
            output.setText(String.format("Perfect conditions, but downward trend. Projected duration is %.0f minutes, %.2f%% chance to get new best", metric, (7.0/8 - percentDiff ) * 100));

    }


                break;
            case 1:

                    if (percentDiff > 0.15)
                        output.setText(String.format("Very unfavorable conditions. Projected distance is %.0f km, %.2f%% chance to get new best", metric, (1.0 / 8 - percentDiff) * 100));
                    else if (percentDiff <= 0.15 && percentDiff >= 0.10) {
                        if (weatherFitnessData.get(
                                weatherFitnessData.size() - 1).durationMA10 > weatherFitnessData.get(weatherFitnessData.size() - 1).durationMA20)
                            output.setText(String.format("Neutral conditions. Upward trend. Projected distance is %.0f km, %.2f%% chance to get new best", metric, (3.0 / 8 - percentDiff) * 100));

                        else
                            output.setText(String.format("Neutral conditions. Downward trend. Projected distance is %.0f km, %.2f%% chance to get new best", metric, (2.0 / 8 - percentDiff) * 100));
                    } else if (percentDiff < 0.10 && percentDiff >= 0.05) {
                        if (weatherFitnessData.get(
                                weatherFitnessData.size() - 1).durationMA10 > weatherFitnessData.get(weatherFitnessData.size() - 1).durationMA20)
                            output.setText(String.format("Favorable conditions. Upward trend. Projected distance is %.0f km, %.2f%% chance to get new best", metric, (4.0 / 8 - percentDiff) * 100));

                        else
                            output.setText(String.format("Favorable conditions, but downward trend. Projected distance is %.0f km, %.2f%% chance to get new best", metric, (3.0 / 8 - percentDiff) * 100));

                    } else if (percentDiff < 0.05 && percentDiff >= 0.0) {
                        if (weatherFitnessData.get(
                                weatherFitnessData.size() - 1).durationMA10 > weatherFitnessData.get(weatherFitnessData.size() - 1).durationMA20)
                            output.setText(String.format("Very favorable conditions. Upward trend. Projected distance is %.0f km, %.2f%% chance to get new best", metric, (6.0 / 8 - percentDiff) * 100));

                        else
                            output.setText(String.format("Very favorable conditions, but downward trend. Projected distance is %.0f km, %.2f%% chance to get new best", metric, (5.0 / 8 - percentDiff) * 100));

                    } else if (metric - best > 0) {
                        if (weatherFitnessData.get(
                                weatherFitnessData.size() - 1).durationMA10 > weatherFitnessData.get(weatherFitnessData.size() - 1).durationMA20)
                            output.setText(String.format("Perfect conditions. Upward trend. Projected distance is %.0f km, %.2f%% chance to get new best", metric, (8.0 / 8 - percentDiff) * 100));
                        else
                            output.setText(String.format("Perfect conditions, but downward trend. Projected distance is %.0f km, %.2f%% chance to get new best", metric, (7.0 / 8 - percentDiff) * 100));
                    }

                break;
            case 2:

                    if (percentDiff > 0.15)
                        output.setText(String.format("Very unfavorable conditions. Projected speed is %.0f km/h, %.2f%% chance to get new best", metric, (1.0 / 8 - percentDiff) * 100));
                    else if (percentDiff <= 0.15 && percentDiff >= 0.10) {
                        if (weatherFitnessData.get(
                                weatherFitnessData.size() - 1).durationMA10 > weatherFitnessData.get(weatherFitnessData.size() - 1).durationMA20)
                            output.setText(String.format("Neutral conditions. Upward trend. Projected speed is %.0f km/h, %.2f%% chance to get new best", metric, (3.0 / 8 - percentDiff) * 100));

                        else
                            output.setText(String.format("Neutral conditions. Downward trend. Projected speed is %.0f km/h, %.2f%% chance to get new best", metric, (2.0 / 8 - percentDiff) * 100));
                    } else if (percentDiff < 0.10 && percentDiff >= 0.05) {
                        if (weatherFitnessData.get(
                                weatherFitnessData.size() - 1).durationMA10 > weatherFitnessData.get(weatherFitnessData.size() - 1).durationMA20)
                            output.setText(String.format("Favorable conditions. Upward trend. Projected speed is %.0f km/h, %.2f%% chance to get new best", metric, (4.0 / 8 - percentDiff) * 100));

                        else
                            output.setText(String.format("Favorable conditions, but downward trend. Projected speed is %.0f km/h, %.2f%% chance to get new best", metric, (3.0 / 8 - percentDiff) * 100));

                    } else if (percentDiff < 0.05 && percentDiff >= 0.0) {
                        if (weatherFitnessData.get(
                                weatherFitnessData.size() - 1).durationMA10 > weatherFitnessData.get(weatherFitnessData.size() - 1).durationMA20)
                            output.setText(String.format("Very favorable conditions. Upward trend. Projected speed is %.0f km/h, %.2f%% chance to get new best", metric, (6.0 / 8 - percentDiff) * 100));

                        else
                            output.setText(String.format("Very favorable conditions, but downward trend. Projected speed is %.0f km/h, %.2f%% chance to get new best", metric, (5.0 / 8 - percentDiff) * 100));

                    } else if (metric - best > 0) {
                        if (weatherFitnessData.get(
                                weatherFitnessData.size() - 1).durationMA10 > weatherFitnessData.get(weatherFitnessData.size() - 1).durationMA20)
                            output.setText(String.format("Perfect conditions. Upward trend. Projected speed is %.0f km/h, %.2f%% chance to get new best", metric, (8.0 / 8 - percentDiff) * 100));
                        else
                            output.setText(String.format("Perfect conditions, but downward trend. Projected speed is %.0f km/h, %.2f%% chance to get new best", metric, (7.0 / 8 - percentDiff) * 100));
                    }

                break;
        }





    }

    static public void generateGraph(int selection) {

        graph.removeAllSeries();
        distance = new LineGraphSeries<DataPoint>();
        daysOff = new LineGraphSeries<DataPoint>();
        duration = new LineGraphSeries<DataPoint>();
        avgSpeed = new LineGraphSeries<DataPoint>();
        temperature = new LineGraphSeries<DataPoint>();
        windSpeed = new LineGraphSeries<DataPoint>();
        MA10 = new LineGraphSeries<DataPoint>();
        MA20 = new LineGraphSeries<DataPoint>();

        distance.setColor(Color.RED);
        duration.setColor(Color.GREEN);
        avgSpeed.setColor(Color.YELLOW);
        temperature.setColor(Color.BLUE);
        windSpeed.setColor(Color.MAGENTA);
        daysOff.setColor(Color.BLACK);
        MA10.setColor(Color.CYAN);
        MA20.setColor(Color.LTGRAY);

        distance.setThickness(2);
        duration.setThickness(2);
        avgSpeed.setThickness(2);
        temperature.setThickness(2);
        windSpeed.setThickness(2);
        daysOff.setThickness(2);
        MA10.setThickness(2);
        MA20.setThickness(2);


        if (selection == 0) {
            for (int i = 0; i < weatherFitnessData.size(); i++) {
                distance.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).distance), true, weatherFitnessData.size());
                duration.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).duration), true, weatherFitnessData.size());
                avgSpeed.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).avgSpeed), true, weatherFitnessData.size());
                temperature.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).temperature), true, weatherFitnessData.size());
                windSpeed.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).windSpeed), true, weatherFitnessData.size());
                daysOff.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).daysOff), true, weatherFitnessData.size());
            }
            graph.addSeries(duration);
            graph.addSeries(distance);
            graph.addSeries(avgSpeed);
            graph.addSeries(temperature);
            graph.addSeries(windSpeed);
            graph.addSeries(daysOff);
        }

        if (selection == 1){
            for (int i = 0; i < weatherFitnessData.size(); i++) {
                duration.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).duration), true, weatherFitnessData.size());
                temperature.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).temperature), true, weatherFitnessData.size());
                windSpeed.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).windSpeed), true, weatherFitnessData.size());
                daysOff.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).daysOff), true, weatherFitnessData.size());
                MA10.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).durationMA10), true, weatherFitnessData.size());
                MA20.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).durationMA20), true, weatherFitnessData.size());
            }
            graph.addSeries(duration);
            graph.addSeries(temperature);
            graph.addSeries(windSpeed);
            graph.addSeries(daysOff);
            graph.addSeries(MA10);
            graph.addSeries(MA20);
        }
        if (selection == 2) {
            distance.setColor(Color.GREEN);
            for (int i = 0; i < weatherFitnessData.size(); i++) {
                distance.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).distance), true, weatherFitnessData.size());
                temperature.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).temperature), true, weatherFitnessData.size());
                windSpeed.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).windSpeed), true, weatherFitnessData.size());
                daysOff.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).daysOff), true, weatherFitnessData.size());
                MA10.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).distanceMA10), true, weatherFitnessData.size());
                MA20.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).distanceMA20), true, weatherFitnessData.size());
            }
            graph.addSeries(distance);
            graph.addSeries(temperature);
            graph.addSeries(windSpeed);
            graph.addSeries(daysOff);
            graph.addSeries(MA10);
            graph.addSeries(MA20);
        }
        if (selection == 3) {
            avgSpeed.setColor(Color.GREEN);
            for (int i = 0; i < weatherFitnessData.size(); i++) {
                avgSpeed.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).avgSpeed), true, weatherFitnessData.size());
                temperature.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).temperature), true, weatherFitnessData.size());
                windSpeed.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).windSpeed), true, weatherFitnessData.size());
                daysOff.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).daysOff), true, weatherFitnessData.size());
                MA10.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).speedMA10), true, weatherFitnessData.size());
                MA20.appendData(new DataPoint(weatherFitnessData.get(i).date,
                        weatherFitnessData.get(i).speedMA20), true, weatherFitnessData.size());
            }
            graph.addSeries(avgSpeed);
            graph.addSeries(temperature);
            graph.addSeries(windSpeed);
            graph.addSeries(daysOff);
            graph.addSeries(MA10);
            graph.addSeries(MA20);
        }
    }


    }
