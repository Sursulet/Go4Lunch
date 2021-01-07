package com.sursulet.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentSnapshot;
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
        MessageHelper.getAllMessageForChat(userSender, uidReceiver).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Message> messages = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Message message = document.toObject(Message.class);
                            messages.add(message);
                        }

                        mutableLiveData.setValue(messages);
                    }
                });

        return mutableLiveData;
    }

    private OnFailureListener onFailureListener() {
        return null;
    }
}
