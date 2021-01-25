package com.sursulet.go4lunch.repository;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sursulet.go4lunch.api.MessageHelper;
import com.sursulet.go4lunch.model.Message;
import com.sursulet.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    private static final String TAG = ChatRepository.class.getSimpleName();

    public void createMessage(String message, User modelCurrentUser, String uidReceiver) {
        String chat = createChatName(modelCurrentUser.getUid(), uidReceiver);


        MessageHelper.createMessageForChat(message, modelCurrentUser, chat);
        //.addOnFailureListener(this.onFailureListener());
    }

    public LiveData<List<Message>> getAllMessages(String uidReceiver) {
        FirebaseUser userSender = FirebaseAuth.getInstance().getCurrentUser();

        //if(user == null) { return; }
        String chat = createChatName(userSender.getUid(), uidReceiver);

        MutableLiveData<List<Message>> mutableLiveData = new MutableLiveData<>();
        MessageHelper.getAllMessageForChat(chat)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(
                            @Nullable QuerySnapshot value,
                            @Nullable FirebaseFirestoreException e
                    ) {
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

                    }
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

    private static String createChatName(String uidSender, String uidReceiver) {
        String name;
        if (uidSender.compareTo(uidReceiver) > 0) name = uidSender + "_" + uidReceiver;
        else name = uidReceiver + "_" + uidSender;
        return name;
        //return (uidSender.compareTo(uidReceiver) > 0) ? uidSender+"_"+uidReceiver : uidReceiver+"_"+uidSender;
    }

}
