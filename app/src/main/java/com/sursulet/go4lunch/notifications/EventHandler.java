package com.sursulet.go4lunch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sursulet.go4lunch.MainActivity;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.api.ActiveRestaurantHelper;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.remote.IGoogleAPIService;
import com.sursulet.go4lunch.remote.RetrofitClient;
import com.sursulet.go4lunch.repository.DetailPlaceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventHandler extends Worker {

    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "GO4LUNCH";

    Restaurant place;
    List<User> users;
    StringBuilder userNames;

    IGoogleAPIService mService = RetrofitClient
            .getClient("https://maps.googleapis.com/")
            .create(IGoogleAPIService.class);

    public EventHandler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //sendVisualNotification("Notification HERE");

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            DetailPlaceRepository detailPlaceRepository = new DetailPlaceRepository();

            try {

                Task<DocumentSnapshot> task = ActiveRestaurantHelper.getActiveRestaurantId(currentUser.getUid())
                        .continueWithTask(new Continuation<QuerySnapshot, Task<QuerySnapshot>>() {
                            @Override
                            public Task<QuerySnapshot> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                                List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                                for (DocumentSnapshot document : task.getResult()) {
                                    Log.d("PEACH", "Continue " + document.getId() + " => " + document.getData());
                                    Log.d("PEACH", "PATH " + document.getReference().getParent().get());
                                    tasks.add(document.getReference().getParent().get());
                                }

                                return tasks.get(0);
                            }
                        })
                        .continueWithTask(new Continuation<QuerySnapshot, Task<DocumentSnapshot>>() {
                            @Override
                            public Task<DocumentSnapshot> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                                if (task.isSuccessful()) {
                                    List<Task<DocumentSnapshot>> snapshots = new ArrayList<>();
                                    users = new ArrayList<>();
                                    userNames = new StringBuilder();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("PEACH", document.getId() + " => " + document.getData());
                                        snapshots.add(document.getReference().getParent().getParent().get());
                                        User user = document.toObject(User.class);
                                        users.add(user);
                                        userNames.append(user.getUsername());
                                    }

                                    return snapshots.get(0);
                                } else {
                                    Log.d("PEACH", "Error getting documents: ", task.getException());
                                }

                                return null;
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                Log.d("PEACH", "onComplete: " + documentSnapshot.getData());
                                place = documentSnapshot.toObject(Restaurant.class);
                                sendVisualNotification("A table! " + place.getName() + " " + userNames);
                            }
                        });

                Tasks.await(task);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.d("PEACH", "doWork: SUCCESS");
        return Result.success();
    }

    public static void oneOffRequest() {
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(EventHandler.class)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .setConstraints(setCons())
                .build();

        WorkManager.getInstance().enqueue(oneTimeWorkRequest);

        //WorkManager workManager = WorkManager.getInstance(MainApplication.getApplication());
        //workManager.enqueue(new OneTimeWorkRequest.Builder(EventHandler.class).build());
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

    private void sendVisualNotification(String messageBody) {
        Log.d("PEACH", "sendVisualNotification: NOTIF");

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getApplicationContext().getString(R.string.notification_title));
        inboxStyle.addLine(messageBody);

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
            CharSequence channelName = "Message provenant de Firebase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManagerCompat.createNotificationChannel(mChannel);
        }

        notificationManagerCompat.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }
}
