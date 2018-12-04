package com.comp354.teamij.fitnesstracking.utils;

import com.comp354.teamij.fitnesstracking.PredictionActivity;
import com.moomeen.endo2java.model.Workout;
import java.util.ArrayList;
import java.util.List;

public class LinearEquationSolver {

    // Source: https://www.sanfoundry.com/java-program-solve-linear-equation/
    static public float[] linearEquationSolver(float[] wind, float[] temperature, int[] rest, double[] metric) {

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

    static public float[] calcCoefficients(int historicalData, int metricData, List<PredictionActivity.WeatherFitnessData> weatherFitnessData) {
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
