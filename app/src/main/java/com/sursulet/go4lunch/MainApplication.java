package com.sursulet.go4lunch;

import android.app.Application;

/**
 * This class is referenced in the AndroidManifest and represent the Application : this is the first class to be created
 * when the user launches the app or "reopen" the app
 */
public class MainApplication extends Application {

    // This is OK because Application is "unique" so we can't leak this context. This wouldn't be allowed for an Activity, tho.
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
