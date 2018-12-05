package com.comp354.teamij.fitnesstracking.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.moomeen.endo2java.EndomondoSession;
import com.moomeen.endo2java.error.InvocationException;
import com.moomeen.endo2java.error.LoginException;
import com.moomeen.endo2java.model.Workout;

import java.util.List;
import java.util.concurrent.ExecutionException;

/***
 * Utilities for Authentication and Login
 * (built as a Singleton)
 */
public class UserUtils {

    // Constants and Variables

    private static UserUtils instance = null;
    private static final String AuthRememberKey = "authenticationRemember";
    private static final String UsernameKey = "username";
    private static final String PasswordKey = "password";

    private static SharedPreferences sharedPreferences = null;

    private static EndomondoSession session;

    /**
     * Get an instance of the UserUtils
     * @param context Application context
     * @return Instance of UserUtils
     */
    public static void setup(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    /**
     * Check if the user has its credentials were saved on the device
     * @return
     */
    public static boolean loginRemembered() {
        return sharedPreferences.getBoolean(UserUtils.AuthRememberKey, false);
    }

    /**
     * Perform a login operation
     * @param username Username
     * @param password Password
     * @return Returns if the login was successful
     */
    public static boolean login(String username, String password, boolean rememberLogin) {
        session = new EndomondoSession(username, password);
        int loginResult = -1;
        try {
            loginResult = new LoginTask().execute().get();
            if (rememberLogin) {
                sharedPreferences.edit().putBoolean(AuthRememberKey, true).apply();
                sharedPreferences.edit().putString(UsernameKey, username).apply();
                sharedPreferences.edit().putString(PasswordKey, password).apply();
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.e("UserUtils","Unhandled exception", e);
            session = null;
        } finally {
         if (loginResult == -1) {
             session = null; // Nullify the session
         }
        }
        return loginResult == 0; // Zero means a successful login
    }

    /**
     * Login with saved credentials
     * @return
     */
    public static boolean login() {
        boolean remember = sharedPreferences.getBoolean(UserUtils.AuthRememberKey, false);
        String username = sharedPreferences.getString(UserUtils.UsernameKey, "");
        String password = sharedPreferences.getString(UserUtils.PasswordKey, "");

        if (!remember ||
                (username.compareToIgnoreCase("") == 0 || password.compareToIgnoreCase("") == 0)) {
            return false;
        } else {
            return login(username, password, false);
        }
    }

    public static void clearCredentials() {
        sharedPreferences.edit().putBoolean(AuthRememberKey, false).apply();
        sharedPreferences.edit().putString(UsernameKey, "").apply();
        sharedPreferences.edit().putString(PasswordKey, "").apply();
        session = null;
    }

    public static EndomondoSession getSession() { return session; }


    private static class LoginTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                session.login();
            } catch (LoginException e) {
                Log.e("UserUtils","Login error", e);
                return -1;
            }
            return 0;
        }
    }
}
