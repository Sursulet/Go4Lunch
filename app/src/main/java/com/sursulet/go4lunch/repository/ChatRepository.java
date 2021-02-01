package com.sursulet.go4lunch.repository;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.api.MessageHelper;
import com.sursulet.go4lunch.model.Message;
import com.sursulet.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    private static final String TAG = ChatRepository.class.getSimpleName();

    private final Application application;
    private final FirebaseAuth firebaseAuth;

    public ChatRepository(Application application, FirebaseAuth firebaseAuth) {
        this.application = application;
        this.firebaseAuth = firebaseAuth;
    }

    public void createMessage(String chat, String message, User userSender) {

        MessageHelper.createMessageForChat(chat, message, userSender)
                .addOnFailureListener(this.onFailureListener());
    }

    public LiveData<List<Message>> getAllMessages(String chat) {

        MutableLiveData<List<Message>> mutableLiveData = new MutableLiveData<>();
        MessageHelper.getAllMessageForChat(chat)
                .addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    List<Message> messages = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        Message message = doc.toObject(Message.class);
                        messages.add(message);
                    }
                    Log.d(TAG, "Current messages: " + messages);
                    mutableLiveData.setValue(messages);

                });
        /*
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Message> messages = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Log.d("PEACH", document.getId() + " => " + document.getData());
                            Message message = document.toObject(Message.class);
                            messages.add(message);
                        }
                        Log.d("PEACH", "onComplete: " + messages.get(0).getMessage());
                        mutableLiveData.setValue(messages);
                    }
                });

         */

        return mutableLiveData;
    }

    public LiveData<String> createChatName(String uidReceiver) {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

        String uidSender = firebaseAuth.getCurrentUser().getUid();
        String name;

        if (uidSender.compareTo(uidReceiver) > 0) name = uidSender + "_" + uidReceiver;
        else name = uidReceiver + "_" + uidSender;

        mutableLiveData.setValue(name);
        return mutableLiveData;
        //return (uidSender.compareTo(uidReceiver) > 0) ? uidSender+"_"+uidReceiver : uidReceiver+"_"+uidSender;
    }

    protected OnFailureListener onFailureListener() {
        return e -> Toast.makeText(
                application,
                application.getString(R.string.error_unknown_error),
                Toast.LENGTH_LONG
        ).show();
    }

}
