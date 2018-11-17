package com.comp354.teamij.fitnesstracking.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.moomeen.endo2java.EndomondoSession;
import com.moomeen.endo2java.error.InvocationException;
import com.moomeen.endo2java.model.Workout;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WorkoutDataFetcher {
    EndomondoSession endomondoSession;

    public WorkoutDataFetcher() {
        endomondoSession = UserUtils.getSession();
    }


    public List<Workout> getWorkouts() {
        try {
            return new GetWorkoutsTask().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e("UserUtils","Unhandled exception", e);
            return Collections.emptyList();
        }
    }

    private class GetWorkoutsTask extends AsyncTask<Void, Void, List<Workout>> {
        @Override
        protected List<Workout> doInBackground(Void... params) {
            try {
                return endomondoSession.getWorkouts();
            } catch (InvocationException e) {
                e.printStackTrace();
                return Collections.emptyList(); //return "unable to get workouts";
            }
        }
    }
}
