package com.sursulet.go4lunch.ui.settings;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;

import com.sursulet.go4lunch.R;

public class SettingsActivity extends AppCompatActivity {

    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);

        if(sharedPref.getDarkModeState()) {
            setTheme(R.style.Theme_Go4Lunch_DayNight);
        } else setTheme(R.style.Theme_Go4Lunch);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        SwitchCompat mySwitch = findViewById(R.id.switch_dark);

        if (sharedPref.getDarkModeState()) {
            mySwitch.setChecked(true);
        }

        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setNightModeState(isChecked);
            restartApp();
        });
    }

    public void restartApp() {
        Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(i);
        finish();
    }
}