package com.sursulet.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.sursulet.go4lunch.model.Message;
import com.sursulet.go4lunch.model.User;

public class MessageHelper {
    private static final String COLLECTION_NAME = "messages";

    // --- CREATE --
    public static Task<DocumentReference> createMessageForChat(String chat, String textMessage, User userSender){

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
}
