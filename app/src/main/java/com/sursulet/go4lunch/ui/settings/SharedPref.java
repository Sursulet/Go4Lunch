package com.sursulet.go4lunch.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences mySharedPref;

    public SharedPref(Context context) {
        mySharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    public void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("DARK_MODE", state);
        editor.apply();
    }

    public Boolean getDarkModeState() {
        return mySharedPref.getBoolean("DARK_MODE", false);
    }
}
