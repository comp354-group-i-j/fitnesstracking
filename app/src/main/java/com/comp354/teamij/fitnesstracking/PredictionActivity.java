package com.comp354.teamij.fitnesstracking;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


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
    static LineGraphSeries<DataPoint> durationMA10 = new LineGraphSeries<DataPoint>();
    static LineGraphSeries<DataPoint> durationMA20 = new LineGraphSeries<DataPoint>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        distance.setColor(Color.RED);
        duration.setColor(Color.GREEN);
        avgSpeed.setColor(Color.YELLOW);
        temperature.setColor(Color.BLUE);
        windSpeed.setColor(Color.MAGENTA);
        daysOff.setColor(Color.BLACK);
        durationMA10.setColor(Color.DKGRAY);
        durationMA20.setColor(Color.LTGRAY);
        distance.setThickness(2);
        duration.setThickness(2);
        avgSpeed.setThickness(2);
        temperature.setThickness(2);
        windSpeed.setThickness(2);
        daysOff.setThickness(2);
        durationMA10.setThickness(2);
        durationMA20.setThickness(2);

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

        final GraphView graph = (GraphView) findViewById(R.id.PredictionGraph);

        Date day0 = workouts.get(workouts.size() - 1).getStartTime().toDate();     // First day
        Date dayN = workouts.get(0).getStartTime().toDate();     // Last day

        syncDataDates(day0, dayN);


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

        generateGraph();

        // 80% historical data
        int historicalData = (int) (weatherFitnessData.size() * 0.8);

        // Trim to modulo 3 = 0
        while (historicalData % 3 != 0)
            historicalData--;

        float[] durationCoeffs = calcCoefficients(historicalData, 0);
        float[] distanceCoeffs = calcCoefficients(historicalData, 1);
        float[] speedCoeffs = calcCoefficients(historicalData, 2);


        int testWind = 5;
        int testTemp = 23;
        int testRest = 1;

        //// Long-term model prediction tests:
        Log.d("Sample case: ", testWind+"*(windCo) + " + testTemp+"*(tempCo) + " + testRest+"*(restCo)");
        Log.d("best duration", durationBest + "");
        Log.d("best distance", distanceBest + "");
        Log.d("best speed", speedBest + "");
        Log.d("list", "********************************************************************************");
        Log.d("list", "DURATION COEFFICIENTS:");
        Log.d("wind", durationCoeffs[0] + "");
        Log.d("temp", durationCoeffs[1] + "");
        Log.d("rest", durationCoeffs[2] + "");
        Log.d("list", "---> PREDICTION:");
        float newDuration = durationCoeffs[0] * testWind + durationCoeffs[1] * testTemp + durationCoeffs[2] * testRest;
        Log.d("list", newDuration + "");
        predictWorkout(newDuration, durationBest);
        Log.d("list", "********************************************************************************");


        Log.d("list", "********************************************************************************");
        Log.d("list", "DISTANCE COEFFICIENTS:");
        Log.d("wind", distanceCoeffs[0] + "");
        Log.d("temp", distanceCoeffs[1] + "");
        Log.d("rest", distanceCoeffs[2] + "");
        Log.d("list", "---> PREDICTION:");
        float newDistance = distanceCoeffs[0] * testWind + distanceCoeffs[1] * testTemp + distanceCoeffs[2] * testRest;
        Log.d("list", newDistance + "");
        predictWorkout(newDistance, distanceBest);
        Log.d("list", "********************************************************************************");

        Log.d("list", "********************************************************************************");
        Log.d("list", "SPEED COEFFICIENTS:");
        Log.d("wind", speedCoeffs[0] + "");
        Log.d("temp", speedCoeffs[1] + "");
        Log.d("rest", speedCoeffs[2] + "");
        Log.d("list", "---> PREDICTION:");
        float newSpeed = speedCoeffs[0] * testWind + speedCoeffs[1] * testTemp + speedCoeffs[2] * testRest;
        Log.d("list", newSpeed + "");
        predictWorkout(newSpeed, speedBest);
        Log.d("list", "********************************************************************************");

        graph.addSeries(daysOff);
        graph.addSeries(durationMA10);
        graph.addSeries(durationMA20);
        graph.addSeries(distance);
        graph.addSeries(duration);
        graph.addSeries(avgSpeed);
        graph.addSeries(temperature);
        graph.addSeries(windSpeed);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space


        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(day0.getTime());
        graph.getViewport().setMaxX(dayN.getTime());
        graph.getViewport().setXAxisBoundsManual(true);
    }

    public class WeatherFitnessData {
        Date date;
        double distance;
        double duration;
        double avgSpeed;
        int daysOff;
        float temperature;
        float windSpeed;
        double durationMA10;
        double durationMA20;
        double distanceMA10;
        double distanceMA20;
        double speedMA10;
        double speedMA20;

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

    public void syncDataDates(Date day0, Date dayN) {
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
                        distanceMA10.getMean(), distanceMA20.getMean(),
                        durationMA10.getMean(), durationMA20.getMean(),
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
                        distanceMA10.getMean(), distanceMA20.getMean(),
                        durationMA10.getMean(), durationMA20.getMean(),
                        speedMA10.getMean(), speedMA20.getMean()));
            }

        }
    }

    public static int getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public void predictWorkout(float metric, double best) {
        if (metric < 0 || (best - metric)/best > 0.15) {
            Log.d("list", "1/8 chance to get new best");
        } else if ((best - metric)/best <= 0.15 && (best - metric)/best >= 0.10) {
            if (weatherFitnessData.get(
                    weatherFitnessData.size() - 1).durationMA10 > weatherFitnessData.get(weatherFitnessData.size() - 1).durationMA20)
                Log.d("list", "3/8 chance to get new best");
            else
                Log.d("list", "2/8 chance to get new best");
        } else if ((best - metric)/best < 0.10 && (best - metric)/best >= 0.05) {
            if (weatherFitnessData.get(
                    weatherFitnessData.size() - 1).durationMA10 > weatherFitnessData.get(weatherFitnessData.size() - 1).durationMA20)
                Log.d("list", "4/8 chance to get new best");
            else
                Log.d("list", "3/8 chance to get new best");
        } else if ((best - metric)/best < 0.05 && (best - metric)/best >= 0.0) {
            if (weatherFitnessData.get(
                    weatherFitnessData.size() - 1).durationMA10 > weatherFitnessData.get(weatherFitnessData.size() - 1).durationMA20)
                Log.d("list", "6/8 chance to get new best");
            else
                Log.d("list", "5/8 chance to get new best");
        } else if (metric - best > 0) {
            if (weatherFitnessData.get(
                    weatherFitnessData.size() - 1).durationMA10 > weatherFitnessData.get(weatherFitnessData.size() - 1).durationMA20)
                Log.d("list", "8/8 chance to get new best");
            else
                Log.d("list", "7/8 chance to get new best");
        }

    }

    public void generateGraph() {
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
            durationMA10.appendData(new DataPoint(weatherFitnessData.get(i).date,
                    weatherFitnessData.get(i).durationMA10), true, weatherFitnessData.size());
            durationMA20.appendData(new DataPoint(weatherFitnessData.get(i).date,
                    weatherFitnessData.get(i).durationMA20), true, weatherFitnessData.size());
        }
    }

    // Source: https://www.sanfoundry.com/java-program-solve-linear-equation/
    public float[] linearEquationSolver(float[] wind, float[] temperature, int[] rest, double[] metric) {

        double[][] mat = new double[3][3];
        double[][] constants = new double[3][1];

        for (int i = 0; i < 3; i++) {
            mat[i][0] = wind[i];
            mat[i][1] = temperature[i];
            mat[i][2] = rest[i];
            constants[i][0] = metric[i];
        }

        //inverse of matrix mat[][]
        double inverted_mat[][] = invert(mat);
        //Multiplication of mat inverse and constants
        double result[][] = new double[3][1];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 1; j++) {
                for (int k = 0; k < 3; k++) {
                    result[i][j] = result[i][j] + inverted_mat[i][k] * constants[k][j];
                }
            }
        }

        float[] output = new float[3];
        output[0] = (float) result[0][0];
        output[1] = (float) result[1][0];
        output[2] = (float) result[2][0];
        return output;
    }

    public float[] calcCoefficients(int historicalData, int metricData) {
        float[] wind = new float[3];
        float[] temp = new float[3];
        int[] rest = new int[3];
        double[] metric = new double[3];

        List<Float> windCoeff = new ArrayList<Float>();
        List<Float> tempCoeff = new ArrayList<Float>();
        List<Float> restCoeff = new ArrayList<Float>();


        for (int i = 0; i < historicalData; i = i + 3) {

            if (weatherFitnessData.get(i).duration == 0)
                continue;

            wind[0] = weatherFitnessData.get(i).windSpeed;
            wind[1] = weatherFitnessData.get(i + 1).windSpeed;
            wind[2] = weatherFitnessData.get(i + 2).windSpeed;

            temp[0] = weatherFitnessData.get(i).temperature;
            temp[1] = weatherFitnessData.get(i + 1).temperature;
            temp[2] = weatherFitnessData.get(i + 2).temperature;

            rest[0] = weatherFitnessData.get(i).daysOff;
            rest[1] = weatherFitnessData.get(i + 1).daysOff;
            rest[2] = weatherFitnessData.get(i + 2).daysOff;

            // Duration
            if (metricData == 0) {
                metric[0] = weatherFitnessData.get(i).duration;
                metric[1] = weatherFitnessData.get(i + 1).duration;
                metric[2] = weatherFitnessData.get(i + 2).duration;
            }
            // Distance
            else if (metricData == 1) {
                metric[0] = weatherFitnessData.get(i).distance;
                metric[1] = weatherFitnessData.get(i + 1).distance;
                metric[2] = weatherFitnessData.get(i + 2).distance;
            }
            // Average speed
            else if (metricData == 2) {
                metric[0] = weatherFitnessData.get(i).avgSpeed;
                metric[1] = weatherFitnessData.get(i + 1).avgSpeed;
                metric[2] = weatherFitnessData.get(i + 2).avgSpeed;
            }

            float[] result = linearEquationSolver(wind, temp, rest, metric);

            if ((!Float.isNaN(result[0]) && !Float.isInfinite(result[0]))
                    && (!Float.isNaN(result[1]) && !Float.isInfinite(result[1]))
                    && (!Float.isNaN(result[2]) && !Float.isInfinite(result[2]))) {
                windCoeff.add(result[0]);
                tempCoeff.add(result[1]);
                restCoeff.add((result[2]));
            }
        }

        float avgWind = 0;
        float avgTemp = 0;
        float avgRest = 0;

        for (int i = 0; i < windCoeff.size(); i++) {
            avgWind += Float.valueOf(windCoeff.get(i));
            avgTemp += Float.valueOf(tempCoeff.get(i));
            avgRest += Float.valueOf(restCoeff.get(i));
        }

        avgWind = avgWind / windCoeff.size();
        avgTemp = avgTemp / tempCoeff.size();
        avgRest = avgRest / restCoeff.size();

        float[] result = new float[3];

        // Coefficients arbitrarily adjusted for more "realistic" results
        result[0] = avgWind*0.3f;
        result[1] = avgTemp*0.35f;
        result[2] = Math.abs(avgRest)*0.075f;

        return result;
    }

    public static double[][] invert(double a[][]) {
        int n = a.length;
        double x[][] = new double[n][n];
        double b[][] = new double[n][n];
        int index[] = new int[n];
        for (int i = 0; i < n; ++i)
            b[i][i] = 1;

        // Transform the matrix into an upper triangle
        gaussian(a, index);

        // Update the matrix b[i][j] with the ratios stored
        for (int i = 0; i < n - 1; ++i)
            for (int j = i + 1; j < n; ++j)
                for (int k = 0; k < n; ++k)
                    b[index[j]][k]
                            -= a[index[j]][i] * b[index[i]][k];

        // Perform backward substitutions
        for (int i = 0; i < n; ++i) {
            x[n - 1][i] = b[index[n - 1]][i] / a[index[n - 1]][n - 1];
            for (int j = n - 2; j >= 0; --j) {
                x[j][i] = b[index[j]][i];
                for (int k = j + 1; k < n; ++k) {
                    x[j][i] -= a[index[j]][k] * x[k][i];
                }
                x[j][i] /= a[index[j]][j];
            }
        }
        return x;
    }

    // Method to carry out the partial-pivoting Gaussian
// elimination.  Here index[] stores pivoting order.
    public static void gaussian(double a[][], int index[]) {
        int n = index.length;
        double c[] = new double[n];

        // Initialize the index
        for (int i = 0; i < n; ++i)
            index[i] = i;

        // Find the rescaling factors, one from each row
        for (int i = 0; i < n; ++i) {
            double c1 = 0;
            for (int j = 0; j < n; ++j) {
                double c0 = Math.abs(a[i][j]);
                if (c0 > c1) c1 = c0;
            }
            c[i] = c1;
        }

        // Search the pivoting element from each column
        int k = 0;
        for (int j = 0; j < n - 1; ++j) {
            double pi1 = 0;
            for (int i = j; i < n; ++i) {
                double pi0 = Math.abs(a[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1) {
                    pi1 = pi0;
                    k = i;
                }
            }

            // Interchange rows according to the pivoting order
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i = j + 1; i < n; ++i) {
                double pj = a[index[i]][j] / a[index[j]][j];

                // Record pivoting ratios below the diagonal
                a[index[i]][j] = pj;

                // Modify other elements accordingly
                for (int l = j + 1; l < n; ++l)
                    a[index[i]][l] -= pj * a[index[j]][l];
            }
        }
    }
}
