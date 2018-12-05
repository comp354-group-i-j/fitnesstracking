package com.comp354.teamij.fitnesstracking;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.comp354.teamij.fitnesstracking.utils.WorkoutDataFetcher;
import com.moomeen.endo2java.model.Workout;

import java.util.List;

public class WorkoutDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_data);

        WorkoutDataFetcher workoutDataFetcher = new WorkoutDataFetcher();

        List<Workout> workouts = workoutDataFetcher.getWorkouts();


        Intent intent = getIntent();
        String temp = intent.getStringExtra(WorkoutsActivity.EXTRA_MESSAGE).replaceAll("\\s+", "");
        int listPos = Integer.parseInt(temp);

        setTitle(workouts.get(listPos).getStartTime().toString().substring(0, 10) + " " + workouts.get(listPos).getStartTime().toString().substring(11, 19));
        
        //distance
        TextView distance = findViewById(R.id.Distance);
        if (workouts.get(listPos).getSpeedAvg() == null)
            distance.setText("Distance\n" + "n/a");
        else
            distance.setText("Distance\n" + String.format("%.2f", workouts.get(listPos).getDistance()) + " km");

        //calories
        TextView calories = findViewById(R.id.Calories);
        if (workouts.get(listPos).getCalories() == null)
            calories.setText("Calories\n" + "n/a");
        else
            calories.setText("Calories\n" + String.format("%.0f", workouts.get(listPos).getCalories()) + " kcal");
        
        //duration
        TextView duration = findViewById(R.id.Duration);
        if (workouts.get(listPos).getDuration() == null)
            duration.setText("Duration\n" + "n/a");
        else
            duration.setText("Duration\n" + workouts.get(listPos).getDuration().getStandardMinutes() + " minutes");

        //avgSpeed
        TextView avgSpeed = findViewById(R.id.AvgSpeed);
        if (workouts.get(listPos).getSpeedAvg() == null)
            avgSpeed.setText("Avg Speed\n" + "n/a");
        else
            avgSpeed.setText("Avg Speed\n" + String.format("%.2f", workouts.get(listPos).getSpeedAvg()) + " km/h");

        //maxSpeed
        TextView maxSpeed = findViewById(R.id.MaxSpeed);
        if (workouts.get(listPos).getSpeedMax() == null)
            maxSpeed.setText("Max Speed\n" + "n/a");
        else
            maxSpeed.setText("Max Speed\n" + String.format("%.2f", workouts.get(listPos).getSpeedMax()) + " km/h");

        //avgPace
        TextView avgPace = findViewById(R.id.AvgPace);
        if (workouts.get(listPos).getSpeedAvg() == null)
            avgPace.setText("Avg Pace\n" + "n/a");
        else
            avgPace.setText("Avg Pace\n" + String.format("%.2f", (1 / workouts.get(listPos).getSpeedAvg() * 60)) + " min/km");

        //maxPace
        TextView maxPace = findViewById(R.id.MaxPace);
        if (workouts.get(listPos).getSpeedMax() == null)
            maxPace.setText("Max Pace\n" + "n/a");
        else
            maxPace.setText("Max Pace\n" + String.format("%.2f", (1 / workouts.get(listPos).getSpeedMax() * 60)) + " min/km");

        //minAltitutude
        TextView minAltitude = findViewById(R.id.MinAltitude);
        if (workouts.get(listPos).getAltitudeMin() == null)
            minAltitude.setText("Min Altitude\n" + "n/a");
        else
            minAltitude.setText("Min Altitude\n" + String.format("%.0f", workouts.get(listPos).getAltitudeMin()) + " m");

        //maxAltitude
        TextView maxAltitude = findViewById(R.id.MaxAltitude);
        if (workouts.get(listPos).getAltitudeMin() == null)
            maxAltitude.setText("Max Altitude\n" + "n/a");
        else
            maxAltitude.setText("Max Altitude\n" + String.format("%.0f", workouts.get(listPos).getAltitudeMax()) + " m");
    }
}
