package com.sursulet.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sursulet.go4lunch.api.MessageHelper;
import com.sursulet.go4lunch.model.Message;
import com.sursulet.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    public void createMessage(String message, User modelCurrentUser, String uidReceiver) {
        MessageHelper.createMessageForChat(message, modelCurrentUser, uidReceiver)
                .addOnFailureListener(this.onFailureListener());
    }

    public LiveData<List<Message>> getAllMessages(String userSender, String uidReceiver) {
        MutableLiveData<List<Message>> mutableLiveData = new MutableLiveData<>();
        MessageHelper.getAllMessageForChat(userSender, uidReceiver).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
            }
        });

        return mutableLiveData;
    }

    private OnFailureListener onFailureListener() {
        return null;
    }

}
