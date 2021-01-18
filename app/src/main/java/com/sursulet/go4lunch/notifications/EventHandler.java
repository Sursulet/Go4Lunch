package com.sursulet.go4lunch.notifications;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class EventHandler extends Worker {
    public EventHandler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return null;
    }

    public static void oneOffRequest() {
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(EventHandler.class)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .setConstraints(setCons())
                .build();

        WorkManager.getInstance().enqueue(oneTimeWorkRequest);
    }

    public static void periodRequest() {
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(EventHandler.class, 10, TimeUnit.SECONDS)
                        .setInitialDelay(5, TimeUnit.SECONDS)
                        .setConstraints(setCons())
                        .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork(
                "periodic",
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicWorkRequest
        );
    }

    private static Constraints setCons() {
        Constraints constraints = new Constraints.Builder().build();
        return constraints;
    }
}
