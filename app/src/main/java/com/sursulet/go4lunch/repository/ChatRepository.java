package com.sursulet.go4lunch.repository;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.sursulet.go4lunch.api.MessageHelper;
import com.sursulet.go4lunch.api.UserHelper;
import com.sursulet.go4lunch.model.Message;
import com.sursulet.go4lunch.model.User;

public class ChatRepository {

    // --------------------
    // REST REQUESTS
    // --------------------
    // 4 - Get Current User from Firestore

    public void getCurrentUserFromFirestore() {
        /*
        UserHelper.getUser(getCurrentUser().getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        modelCurrentUser = documentSnapshot.toObject(User.class);
                    }
                });

         */
    }

    // 6 - Create options for RecyclerView from a Query
    /*
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }*/

    public void getMessages() {
        //MessageHelper.getAllMessageForChat(this.currentChatName);
    }
}
