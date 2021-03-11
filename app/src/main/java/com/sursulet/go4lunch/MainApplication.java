package com.sursulet.go4lunch;

import android.app.Application;

/**
 * This class is referenced in the AndroidManifest and represent the Application : this is the first class to be created
 * when the user launches the app or "reopen" the app
 */
public class MainApplication extends Application {

    private static Application sApplication;

    @Override
    public void onCreate() {
        super.onCreate();

        sApplication = this;
    }

    public static Application getApplication() {
        return sApplication;
    }
}
