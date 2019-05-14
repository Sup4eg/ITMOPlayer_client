package com.example.itmoplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void set_user(String username) {
        prefs.edit().putString("user_name", username).apply();
    }

    public String get_user() {
        String user_name = prefs.getString("user_name","");
        return user_name;
    }


    public void set_user_password(String password) {
        prefs.edit().putString("password", password).apply();
    }

    public String get_user_password() {
        String password = prefs.getString("password","");
        return password;
    }

}
