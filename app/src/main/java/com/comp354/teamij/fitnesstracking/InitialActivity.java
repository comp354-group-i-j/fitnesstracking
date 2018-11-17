package com.comp354.teamij.fitnesstracking;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.comp354.teamij.fitnesstracking.utils.UserUtils;

/***
 * Initial Activity to prompt for login if necessary
 */
public class InitialActivity extends AppCompatActivity {

    private UserUtils userUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent activityIntent;
        UserUtils.setup(getApplicationContext());

        // Check if login is required before showing MainActivity
        if (UserUtils.loginRemembered()) {
            activityIntent = new Intent(getApplicationContext(), MainActivity.class);
        } else {
            activityIntent = new Intent(getApplicationContext(), LoginActivity.class);
        }

        startActivity(activityIntent);
        finish();
    }
}