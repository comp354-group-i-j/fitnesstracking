package com.comp354.teamij.fitnesstracking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.comp354.teamij.fitnesstracking.utils.WorkoutDataFetcher;
import com.moomeen.endo2java.model.Workout;

import java.util.List;
import java.text.SimpleDateFormat;

public class WorkoutsActivity extends AppCompatActivity {

    ListView workoutList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_workouts);

        setTitle("Workouts");
        workoutList = findViewById(R.id.workoutList);

        CustomAdapter customAdapter = new CustomAdapter();
        workoutList.setAdapter(customAdapter);

        workoutList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                goToWorkoutData(v, position);
// assuming string and if you want to get the value on click of list item
                // do what you intend to do on click of listview row
            }
        });



    }

    public static final String EXTRA_MESSAGE = "com.comp354.teamij.fitnesstracking.MESSAGE";

    public void goToWorkoutData(View view, int position) {
        Intent intent = new Intent(this, WorkoutDataActivity.class);
        intent.putExtra(EXTRA_MESSAGE, position + "");
        startActivity(intent);
    }

    class CustomAdapter extends BaseAdapter{

        List<Workout> workouts;
        WorkoutDataFetcher workoutDataFetcher = new WorkoutDataFetcher();

        public CustomAdapter() {
            super();
            workouts = workoutDataFetcher.getWorkouts();
        }

        @Override
        public int getCount() {
            return workouts.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.listoption,null);
            TextView workoutDate = view.findViewById(R.id.workoutDate);
            TextView workoutDuration = view.findViewById(R.id.workoutDuration);
            workoutDate.setText(workouts.get(i).getStartTime().toDate().toString()/*toString().substring(0,10) + " " + workouts.get(i).getStartTime().toString().substring(11,19)*/);
            workoutDuration.setText(workouts.get(i).getDuration().getStandardMinutes() + " minutes");

            return view;
        }
    }
}
