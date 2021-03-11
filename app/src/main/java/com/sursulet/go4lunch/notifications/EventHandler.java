package com.sursulet.go4lunch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sursulet.go4lunch.MainActivity;
import com.sursulet.go4lunch.MainApplication;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class EventHandler extends Worker {

    private static final String TAG = EventHandler.class.getSimpleName();

    public EventHandler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            try {

                Task<DocumentSnapshot> task = FirebaseFirestore.getInstance()
                        .collection(LocalDate.now() + "_UsersActiveRestaurants")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .get();

                Tasks.await(task);
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Restaurant restaurant = document.toObject(Restaurant.class);
                        if (restaurant != null) {

                            Task<QuerySnapshot> listTask = FirebaseFirestore.getInstance()
                                    .collection(LocalDate.now() + "_activeRestaurants")
                                    .get();

                            Tasks.await(listTask);

                            if (listTask.isSuccessful()) {
                                StringBuilder workmates = new StringBuilder();
                                for (QueryDocumentSnapshot snapshot : listTask.getResult()) {
                                    Log.d(TAG, snapshot.getId() + " => " + snapshot.getData());
                                    User user = snapshot.toObject(User.class);
                                    if(!(user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))){
                                        workmates.append(user.getUsername());
                                    }
                                }

                                if (workmates.length() == 0)
                                    workmates.append("No colleagues go to this restaurant");

                                sendVisualNotification(restaurant.getName(), restaurant.getAddress(), workmates);

                            } else {
                                Log.d(TAG, "Error getting documents: ", listTask.getException());
                            }
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Result.success();
    }

    public static void periodRequest() {
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(EventHandler.class, 1, TimeUnit.DAYS)
                        .setInitialDelay(setDelay(), TimeUnit.MINUTES)
                        .setConstraints(setCons())
                        .build();

        WorkManager workManager = WorkManager.getInstance(
                MainApplication.getApplication().getApplicationContext());

        workManager.enqueueUniquePeriodicWork(
                "periodic",
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicWorkRequest
        );
    }

    private static int setDelay() {
        if (LocalTime.now().getHour() < 11) {
            return (11 - LocalTime.now().getHour()) * 60 + 60 - LocalTime.now().getMinute();
        } else if (LocalTime.now().getHour() < 12) {
            return 60 - LocalTime.now().getMinute();
        } else if (LocalTime.now().getHour() > 12) {
            return (23 - LocalTime.now().getHour()) * 60 + 60 - LocalTime.now().getMinute() + 720;
        } else {
            return 0;
        }
    }

    private static Constraints setCons() {
        return new Constraints.Builder().build();
    }

    private void sendVisualNotification(String restaurantName, String restaurantAddress, StringBuilder workmates) {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getApplicationContext().getString(R.string.notification_title));
        inboxStyle.addLine("A table! ").addLine(restaurantName).addLine(restaurantAddress).addLine(workmates);

        String channelId = getApplicationContext().getString(R.string.default_notification_channel_id);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.ic_hot_food_in_a_bowl)
                        .setContentTitle(getApplicationContext().getString(R.string.app_name))
                        .setContentText(getApplicationContext().getString(R.string.notification_title))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);

            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);

            notificationManagerCompat.createNotificationChannel(mChannel);
        }

        notificationManagerCompat.notify("GO4LUNCH", 7, notificationBuilder.build());
    }
}
