package com.comp354.teamij.fitnesstracking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.comp354.teamij.fitnesstracking.utils.UserUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private Button loginButton;
    private CheckBox rememberMeCheckBox;
    private UserUtils userUtils;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.login_button);
        usernameField = (EditText) findViewById(R.id.username_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        rememberMeCheckBox = (CheckBox) findViewById(R.id.remember_check_box);
        UserUtils.setup(getApplicationContext());


        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent activityIntent;
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();
                if (UserUtils.login(username, password, rememberMeCheckBox.isChecked())) {
                    activityIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(activityIntent);
                    finish();
                }
            }
        });
    }
}