package com.comp354.teamij.fitnesstracking;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.comp354.teamij.fitnesstracking.utils.MovingAverage;
import com.comp354.teamij.fitnesstracking.R;
import com.comp354.teamij.fitnesstracking.utils.WorkoutDataFetcher;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.moomeen.endo2java.model.Workout;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FitnessGraphsActivity extends AppCompatActivity {

    EditText min, max;
    double min1,max1;
    LineGraphSeries<DataPoint> series;
    LineGraphSeries<DataPoint> MA10;
    LineGraphSeries<DataPoint> MA20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_graphs);

        WorkoutDataFetcher workoutDataFetcher = new WorkoutDataFetcher();

        final List<Workout> workouts = workoutDataFetcher.getWorkouts();

        setTitle("Graphs");


        min = findViewById(R.id.min);
        max = findViewById(R.id.max);

        final GraphView graph = (GraphView) findViewById(R.id.Graph);
        graph.getGridLabelRenderer().setNumHorizontalLabels(workouts.size());

        Button distance = findViewById(R.id.Distance);
        Button duration = findViewById(R.id.Duration);
        Button calories = findViewById(R.id.Calories);
        Button avgSpeed = findViewById(R.id.AvgSpeed);
        Button maxSpeed = findViewById(R.id.MaxSpeed);
        Button avgPace = findViewById(R.id.AvgPace);
        Button maxPace = findViewById(R.id.MaxPace);
        Button minAltitude = findViewById(R.id.MinAltitude);
        Button maxAltitude = findViewById(R.id.MaxAltitude);

        distance.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        setTitle("Distance vs. Time");

                        if (min.getText().toString().matches(""))
                            min1=0;
                        else
                            min1=Double.parseDouble(min.getText().toString());
                        if (max.getText().toString().matches(""))
                            max1=Double.MAX_VALUE;
                        else
                            max1=Double.parseDouble(max.getText().toString());

                        graph.removeAllSeries();
                        series = new LineGraphSeries<DataPoint>();
                        series.setColor(Color.BLUE);
                        MA10 = new LineGraphSeries<DataPoint>();
                        MA10.setColor(Color.GREEN);
                        MA20 = new LineGraphSeries<DataPoint>();
                        MA20.setColor(Color.RED);

                        int x = 0;
                        Double y = 0.0;

                        MovingAverage obj = new MovingAverage(10);
                        MovingAverage obj2 = new MovingAverage(20);

                        for(int i = 0; i < workouts.size(); i++){

                            x = i;
                            y = workouts.get(i).getDistance();
                            if (y>=min1 && y<=max1)
                            {
                                obj.addData(y);
                                obj.getMean();
                                obj2.addData(y);
                                obj2.getMean();
                                series.appendData(new DataPoint(x,y), true, workouts.size());
                                MA10.appendData(new DataPoint(x,obj.getMean()), true, workouts.size());
                                MA20.appendData(new DataPoint(x,obj2.getMean()), true, workouts.size());
                            }
                        }
                        graph.addSeries(series);
                        graph.addSeries(MA10);
                        graph.addSeries(MA20);

                    }
                });

        duration.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        setTitle("Duration vs. Time");

                        if (min.getText().toString().matches(""))
                            min1=0;
                        else
                            min1=Double.parseDouble(min.getText().toString());
                        if (max.getText().toString().matches(""))
                            max1=Double.MAX_VALUE;
                        else
                            max1=Double.parseDouble(max.getText().toString());
                        graph.removeAllSeries();
                        series = new LineGraphSeries<DataPoint>();
                        series.setColor(Color.BLUE);
                        MA10 = new LineGraphSeries<DataPoint>();
                        MA10.setColor(Color.GREEN);
                        MA20 = new LineGraphSeries<DataPoint>();
                        MA20.setColor(Color.RED);

                        int x = 0;
                        Double y = 0.0;

                        MovingAverage obj = new MovingAverage(10);
                        MovingAverage obj2 = new MovingAverage(20);

                        for(int i = 0; i < workouts.size(); i++){

                            x = i;
                            y = workouts.get(i).getDuration().getStandardMinutes()+0.0;
                            if (y>=min1 && y<=max1)
                            {
                                obj.addData(y);
                                obj.getMean();
                                obj2.addData(y);
                                obj2.getMean();
                                series.appendData(new DataPoint(x,y), true, workouts.size());
                                MA10.appendData(new DataPoint(x,obj.getMean()), true, workouts.size());
                                MA20.appendData(new DataPoint(x,obj2.getMean()), true, workouts.size());
                            }
                        }
                        graph.addSeries(series);
                        graph.addSeries(MA10);
                        graph.addSeries(MA20);

                    }
                });

        calories.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        setTitle("Calories vs. Time");

                        if (min.getText().toString().matches(""))
                            min1=0;
                        else
                            min1=Double.parseDouble(min.getText().toString());
                        if (max.getText().toString().matches(""))
                            max1=Double.MAX_VALUE;
                        else
                            max1=Double.parseDouble(max.getText().toString());
                        graph.removeAllSeries();
                        series = new LineGraphSeries<DataPoint>();
                        series.setColor(Color.BLUE);
                        MA10 = new LineGraphSeries<DataPoint>();
                        MA10.setColor(Color.GREEN);
                        MA20 = new LineGraphSeries<DataPoint>();
                        MA20.setColor(Color.RED);

                        int x = 0;
                        Double y = 0.0;

                        MovingAverage obj = new MovingAverage(10);
                        MovingAverage obj2 = new MovingAverage(20);

                        for(int i = 0; i < workouts.size(); i++){

                            x = i;
                            y = workouts.get(i).getCalories();
                            if (y>=min1 && y<=max1)
                            {
                                obj.addData(y);
                                obj.getMean();
                                obj2.addData(y);
                                obj2.getMean();
                                series.appendData(new DataPoint(x,y), true, workouts.size());
                                MA10.appendData(new DataPoint(x,obj.getMean()), true, workouts.size());
                                MA20.appendData(new DataPoint(x,obj2.getMean()), true, workouts.size());
                            }
                        }
                        graph.addSeries(series);
                        graph.addSeries(MA10);
                        graph.addSeries(MA20);

                    }
                });

        avgSpeed.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        setTitle("Avg Speed vs. Time");

                        if (min.getText().toString().matches(""))
                            min1=0;
                        else
                            min1=Double.parseDouble(min.getText().toString());
                        if (max.getText().toString().matches(""))
                            max1=Double.MAX_VALUE;
                        else
                            max1=Double.parseDouble(max.getText().toString());
                        graph.removeAllSeries();
                        series = new LineGraphSeries<DataPoint>();
                        series.setColor(Color.BLUE);
                        MA10 = new LineGraphSeries<DataPoint>();
                        MA10.setColor(Color.GREEN);
                        MA20 = new LineGraphSeries<DataPoint>();
                        MA20.setColor(Color.RED);

                        int x = 0;
                        Double y = 0.0;

                        MovingAverage obj = new MovingAverage(10);
                        MovingAverage obj2 = new MovingAverage(20);

                        for(int i = 0; i < workouts.size(); i++){
                            if(workouts.get(i).getSpeedAvg() == null)
                                continue;
                            x = i;
                            y = workouts.get(i).getSpeedAvg();
                            if (y>=min1 && y<=max1)
                            {
                                obj.addData(y);
                                obj.getMean();
                                obj2.addData(y);
                                obj2.getMean();
                                series.appendData(new DataPoint(x,y), true, workouts.size());
                                MA10.appendData(new DataPoint(x,obj.getMean()), true, workouts.size());
                                MA20.appendData(new DataPoint(x,obj2.getMean()), true, workouts.size());
                            }
                        }
                        graph.addSeries(series);
                        graph.addSeries(MA10);
                        graph.addSeries(MA20);

                    }
                });

        maxSpeed.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        setTitle("Max Speed vs. Time");

                        if (min.getText().toString().matches(""))
                            min1=0;
                        else
                            min1=Double.parseDouble(min.getText().toString());
                        if (max.getText().toString().matches(""))
                            max1=Double.MAX_VALUE;
                        else
                            max1=Double.parseDouble(max.getText().toString());
                        graph.removeAllSeries();
                        series = new LineGraphSeries<DataPoint>();
                        series.setColor(Color.BLUE);
                        MA10 = new LineGraphSeries<DataPoint>();
                        MA10.setColor(Color.GREEN);
                        MA20 = new LineGraphSeries<DataPoint>();
                        MA20.setColor(Color.RED);

                        int x = 0;
                        Double y = 0.0;

                        MovingAverage obj = new MovingAverage(10);
                        MovingAverage obj2 = new MovingAverage(20);

                        for(int i = 0; i < workouts.size(); i++){
                            if(workouts.get(i).getSpeedMax() == null)
                                continue;
                            x = i;
                            y = workouts.get(i).getSpeedMax();
                            if (y>=min1 && y<=max1)
                            {
                                obj.addData(y);
                                obj.getMean();
                                obj2.addData(y);
                                obj2.getMean();
                                series.appendData(new DataPoint(x,y), true, workouts.size());
                                MA10.appendData(new DataPoint(x,obj.getMean()), true, workouts.size());
                                MA20.appendData(new DataPoint(x,obj2.getMean()), true, workouts.size());
                            }
                        }
                        graph.addSeries(series);
                        graph.addSeries(MA10);
                        graph.addSeries(MA20);

                    }
                });

        avgPace.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        setTitle("Avg Pace vs. Time");

                        if (min.getText().toString().matches(""))
                            min1=0;
                        else
                            min1=Double.parseDouble(min.getText().toString());
                        if (max.getText().toString().matches(""))
                            max1=Double.MAX_VALUE;
                        else
                            max1=Double.parseDouble(max.getText().toString());
                        graph.removeAllSeries();
                        series = new LineGraphSeries<DataPoint>();
                        series.setColor(Color.BLUE);
                        MA10 = new LineGraphSeries<DataPoint>();
                        MA10.setColor(Color.GREEN);
                        MA20 = new LineGraphSeries<DataPoint>();
                        MA20.setColor(Color.RED);

                        int x = 0;
                        Double y = 0.0;

                        MovingAverage obj = new MovingAverage(10);
                        MovingAverage obj2 = new MovingAverage(20);

                        for(int i = 0; i < workouts.size(); i++){
                            if(workouts.get(i).getSpeedAvg() == null)
                                continue;
                            x = i;
                            y = 1/workouts.get(i).getSpeedAvg()*60;
                            if (y>=min1 && y<=max1)
                            {
                                obj.addData(y);
                                obj.getMean();
                                obj2.addData(y);
                                obj2.getMean();
                                series.appendData(new DataPoint(x,y), true, workouts.size());
                                MA10.appendData(new DataPoint(x,obj.getMean()), true, workouts.size());
                                MA20.appendData(new DataPoint(x,obj2.getMean()), true, workouts.size());
                            }
                        }
                        graph.addSeries(series);
                        graph.addSeries(MA10);
                        graph.addSeries(MA20);

                    }
                });

        maxPace.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        setTitle("Max Pace vs. Time");

                        if (min.getText().toString().matches(""))
                            min1=0;
                        else
                            min1=Double.parseDouble(min.getText().toString());
                        if (max.getText().toString().matches(""))
                            max1=Double.MAX_VALUE;
                        else
                            max1=Double.parseDouble(max.getText().toString());
                        graph.removeAllSeries();
                        series = new LineGraphSeries<DataPoint>();
                        series.setColor(Color.BLUE);
                        MA10 = new LineGraphSeries<DataPoint>();
                        MA10.setColor(Color.GREEN);
                        MA20 = new LineGraphSeries<DataPoint>();
                        MA20.setColor(Color.RED);

                        int x = 0;
                        Double y = 0.0;

                        MovingAverage obj = new MovingAverage(10);
                        MovingAverage obj2 = new MovingAverage(20);

                        for(int i = 0; i < workouts.size(); i++){
                            if(workouts.get(i).getSpeedMax() == null)
                                continue;
                            x = i;
                            y = 1/workouts.get(i).getSpeedMax()*60;
                            if (y>=min1 && y<=max1)
                            {
                                obj.addData(y);
                                obj.getMean();
                                obj2.addData(y);
                                obj2.getMean();
                                series.appendData(new DataPoint(x,y), true, workouts.size());
                                MA10.appendData(new DataPoint(x,obj.getMean()), true, workouts.size());
                                MA20.appendData(new DataPoint(x,obj2.getMean()), true, workouts.size());
                            }
                        }
                        graph.addSeries(series);
                        graph.addSeries(MA10);
                        graph.addSeries(MA20);

                    }
                });

        minAltitude.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        setTitle("Min Altitude vs. Time");

                        if (min.getText().toString().matches(""))
                            min1=0;
                        else
                            min1=Double.parseDouble(min.getText().toString());
                        if (max.getText().toString().matches(""))
                            max1=Double.MAX_VALUE;
                        else
                            max1=Double.parseDouble(max.getText().toString());
                        graph.removeAllSeries();
                        series = new LineGraphSeries<DataPoint>();
                        series.setColor(Color.BLUE);
                        MA10 = new LineGraphSeries<DataPoint>();
                        MA10.setColor(Color.GREEN);
                        MA20 = new LineGraphSeries<DataPoint>();
                        MA20.setColor(Color.RED);

                        int x = 0;
                        Double y = 0.0;

                        MovingAverage obj = new MovingAverage(10);
                        MovingAverage obj2 = new MovingAverage(20);

                        for(int i = 0; i < workouts.size(); i++){
                            if(workouts.get(i).getAltitudeMin() == null)
                                continue;
                            x = i;
                            y = workouts.get(i).getAltitudeMin();
                            if (y>=min1 && y<=max1)
                            {
                                obj.addData(y);
                                obj.getMean();
                                obj2.addData(y);
                                obj2.getMean();
                                series.appendData(new DataPoint(x,y), true, workouts.size());
                                MA10.appendData(new DataPoint(x,obj.getMean()), true, workouts.size());
                                MA20.appendData(new DataPoint(x,obj2.getMean()), true, workouts.size());
                            }
                        }
                        graph.addSeries(series);
                        graph.addSeries(MA10);
                        graph.addSeries(MA20);

                    }
                });

        maxAltitude.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        setTitle("Max Altitude vs. Time");

                        if (min.getText().toString().matches(""))
                            min1=0;
                        else
                            min1=Double.parseDouble(min.getText().toString());
                        if (max.getText().toString().matches(""))
                            max1=Double.MAX_VALUE;
                        else
                            max1=Double.parseDouble(max.getText().toString());
                        graph.removeAllSeries();
                        series = new LineGraphSeries<DataPoint>();
                        series.setColor(Color.BLUE);
                        MA10 = new LineGraphSeries<DataPoint>();
                        MA10.setColor(Color.GREEN);
                        MA20 = new LineGraphSeries<DataPoint>();
                        MA20.setColor(Color.RED);

                        int x = 0;
                        Double y = 0.0;

                        MovingAverage obj = new MovingAverage(10);
                        MovingAverage obj2 = new MovingAverage(20);

                        for(int i = 0; i < workouts.size(); i++){
                            if(workouts.get(i).getAltitudeMax() == null)
                                continue;
                            x = i;
                            y = workouts.get(i).getAltitudeMax();
                            if (y>=min1 && y<=max1)
                            {
                                obj.addData(y);
                                obj.getMean();
                                obj2.addData(y);
                                obj2.getMean();
                                series.appendData(new DataPoint(x,y), true, workouts.size());
                                MA10.appendData(new DataPoint(x,obj.getMean()), true, workouts.size());
                                MA20.appendData(new DataPoint(x,obj2.getMean()), true, workouts.size());
                            }

                        }
                        graph.addSeries(series);
                        graph.addSeries(MA10);
                        graph.addSeries(MA20);

                    }
                });

    }



}
