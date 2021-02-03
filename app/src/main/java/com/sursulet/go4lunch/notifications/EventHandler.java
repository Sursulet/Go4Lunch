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
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sursulet.go4lunch.MainActivity;
import com.sursulet.go4lunch.MainApplication;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.api.ActiveRestaurantHelper;
import com.sursulet.go4lunch.model.Restaurant;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;
import com.sursulet.go4lunch.repository.DetailPlaceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;

public class EventHandler extends Worker {

    private String address;
    private StringBuilder workmates;

    public EventHandler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            //Parmi les collegues enlever le current user
            //Si il n'y a pas de resto choisi.

            try {

                Task<DocumentSnapshot> task = ActiveRestaurantHelper.getActiveRestaurantId(currentUser.getUid())
                        .continueWithTask(queryTask -> {
                            List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                            for (DocumentSnapshot document : queryTask.getResult()) {
                                tasks.add(document.getReference().getParent().get());
                            }

                            return tasks.get(0);
                        })
                        .continueWithTask(querySnapshotTask -> {
                            if (querySnapshotTask.isSuccessful()) {
                                List<Task<DocumentSnapshot>> snapshots = new ArrayList<>();
                                workmates = new StringBuilder();
                                for (QueryDocumentSnapshot document : querySnapshotTask.getResult()) {

                                    User user = document.toObject(User.class);
                                    workmates.append(user.getUsername());

                                    if (document.getReference().getParent().getParent() != null) {
                                        snapshots.add(document.getReference().getParent().getParent().get());
                                    }
                                }

                                return snapshots.get(0);
                            } else {
                                Log.d("PEACH", "Error getting documents: ", querySnapshotTask.getException());
                                return null;
                            }
                        });

                Tasks.await(task);
                Restaurant restaurant = task.getResult().toObject(Restaurant.class);

                if (restaurant != null) {
                    DetailPlaceRepository detailPlaceRepository = new DetailPlaceRepository();
                    Response<GooglePlacesDetailResult> response = detailPlaceRepository.getDetailPlaceSync(restaurant.getId());
                    GooglePlacesDetailResult detailResult = response.body();

                    if (detailResult != null) {
                        address = detailResult.getResult().getFormattedAddress();
                    }
                    //address = detailResult != null ? detailResult.getResult().getFormattedAddress() : null;

                    sendVisualNotification(restaurant.getName(), address, workmates);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Result.success();
    }

    public static void periodRequest() {
        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(EventHandler.class, 10, TimeUnit.SECONDS)
                        .setInitialDelay(5, TimeUnit.SECONDS)
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
            notificationManagerCompat.createNotificationChannel(mChannel);
        }

        notificationManagerCompat.notify("GO4LUNCH", 7, notificationBuilder.build());
    }
}
