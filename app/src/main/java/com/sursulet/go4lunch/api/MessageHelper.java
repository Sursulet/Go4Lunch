package com.sursulet.go4lunch.api;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.sursulet.go4lunch.model.Message;
import com.sursulet.go4lunch.model.User;

import java.util.HashMap;
import java.util.Map;

public class MessageHelper {
    private static final String COLLECTION_NAME = "messages";

    // --- CREATE --
    public static Task<DocumentReference> createMessageForChat(String textMessage, User userSender, String chat){
        Log.d("PEACH", "createMessageForChat: " + textMessage + "/" + userSender.getUid() + "/" + chat);

        Message message = new Message(textMessage, userSender);
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .add(message);
    }



    // --- GET ---

    public static Query getAllMessageForChat(String chat){

        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }

    // -- UPDATE ---

    /*
    public static Task<Void> updateDate(String textMessage, User userSender, String chat) {
        Message message = new Message(textMessage, userSender);
        Map<String,Object> updates = new HashMap<>();
        updates.put("timestamp", FieldValue.serverTimestamp());

        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .document(message)
                .update(updates);
    }

     */
}
