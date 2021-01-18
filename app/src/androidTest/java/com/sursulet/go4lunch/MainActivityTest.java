package com.sursulet.go4lunch;

import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {

    @Rule
    public final ActivityScenario<MainActivity> activityTestRule = ActivityScenario.launch(MainActivity.class);

    public void createUser() throws InterruptedException {
        //
    }

}