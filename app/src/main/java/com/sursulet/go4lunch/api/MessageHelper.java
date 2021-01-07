package com.sursulet.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.sursulet.go4lunch.model.Message;
import com.sursulet.go4lunch.model.User;

public class MessageHelper {
    private static final String COLLECTION_NAME = "messages";

    // --- CREATE --
    public static Task<DocumentReference> createMessageForChat(String textMessage, User userSender, String uidReceiver){

        String chat = createChatName(userSender.getUid(), uidReceiver);
        Message message = new Message(textMessage, userSender);

        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .add(message);
    }

    private static String createChatName(String uidSender, String uidReceiver) {
        String name;
        if(uidSender.compareTo(uidReceiver) > 0) name = uidSender+"_"+uidReceiver;
        else name = uidReceiver+"_"+uidSender;
        return name;
        //return (uidSender.compareTo(uidReceiver) > 0) ? uidSender+"_"+uidReceiver : uidReceiver+"_"+uidSender;
    }

    // --- GET ---

    public static Query getAllMessageForChat(String userSender, String uidReceiver){
        String chat = createChatName(userSender, uidReceiver);
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }
}
