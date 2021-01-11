package com.sursulet.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.sursulet.go4lunch.api.MessageHelper;
import com.sursulet.go4lunch.model.Message;
import com.sursulet.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    public void createMessage(String message, User modelCurrentUser, String uidReceiver) {
        String chat = createChatName(modelCurrentUser.getUid(), uidReceiver);


        MessageHelper.createMessageForChat(message, modelCurrentUser, chat);
                //.addOnFailureListener(this.onFailureListener());
    }

    public LiveData<List<Message>> getAllMessages(String userSender, String uidReceiver) {
        String chat = createChatName(userSender, uidReceiver);

        MutableLiveData<List<Message>> mutableLiveData = new MutableLiveData<>();
        MessageHelper.getAllMessageForChat(chat)
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

        return mutableLiveData;
    }

    private static String createChatName(String uidSender, String uidReceiver) {
        String name;
        if(uidSender.compareTo(uidReceiver) > 0) name = uidSender+"_"+uidReceiver;
        else name = uidReceiver+"_"+uidSender;
        return name;
        //return (uidSender.compareTo(uidReceiver) > 0) ? uidSender+"_"+uidReceiver : uidReceiver+"_"+uidSender;
    }

}
