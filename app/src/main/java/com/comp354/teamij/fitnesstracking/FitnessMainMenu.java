package com.comp354.teamij.fitnesstracking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class FitnessMainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_main_menu);
        //
        setTitle("Fitness Data");
        // Get the Intent that started this activity and extract the string
        //Intent intent = getIntent();
        // String message = intent.getStringExtra(EXTRA_MESSAGE);

        //TextView loginMessage = findViewById(R.id.LoginMessage);
        //loginMessage.setText(message);

        // Button in the sub-menu of fitness data to display workouts data activity page
        Button viewWorkouts = findViewById(R.id.viewWorkouts);

        viewWorkouts.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view) {

                        goToViewWorkouts(view);


                    }
                });
        // button to display the graphs activity page
        Button viewGraphs = findViewById(R.id.viewGraphs);

        viewGraphs.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view) {
                        goToViewGraphs(view);

                    }
                });
    }
    // listener of the buton to direct to workout activity page
    public void goToViewWorkouts(View view) {
        Intent intent = new Intent(this, WorkoutsActivity.class);
        startActivity(intent);
    }
    // listener to direct button to fitness graphs ativity
    public void goToViewGraphs(View view) {
        Intent intent = new Intent(this, FitnessGraphsActivity.class);
        startActivity(intent);
    }
}
