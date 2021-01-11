package com.sursulet.go4lunch.ui.chat;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sursulet.go4lunch.model.Message;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.repository.ChatRepository;
import com.sursulet.go4lunch.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    MutableLiveData<List<MessageUiModel>> uiModelMutableLiveData = new MutableLiveData<>();
    LiveData<List<MessageUiModel>> allMessagesLiveData;

    LiveData<User> userReceiver;
    LiveData<User> currentUser;
    private User modelCurrentUser;

    public ChatViewModel(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public void init(String uidReceiver) {
        userReceiver = userRepository.getUser(uidReceiver);
        Log.d("PEACH", "init: " + uidReceiver + "/");
        allMessagesLiveData = Transformations.map(
                chatRepository.getAllMessages(FirebaseAuth.getInstance().getCurrentUser().getUid(), uidReceiver),
                messages -> {
                    List<MessageUiModel> results = new ArrayList<>();
                    for (Message message : messages) {
                        Log.d("PEACH", "init: " + message.getMessage());
                        MessageUiModel messageUiModel = new MessageUiModel(
                                message.getMessage(),
                                message.getDateCreated().toString(),
                                message.getUserSender()
                        );

                        results.add(messageUiModel);
                    }
                    Log.d("PEACH", "init: results " + results.get(0).getMessage());
                    return results;
                });
    }

    LiveData<User> getUserReceiver() { return userReceiver; }
    LiveData<List<MessageUiModel>> getUiModelMutableLiveData() {
        return allMessagesLiveData;
    }

    public void onSendMessage(String message, String uidSender, String uidReceiver) {
        //currentUser = userRepository.getUser(uidSender);
        //userReceiver = userRepository.getUser(uidReceiver);
        assert FirebaseAuth.getInstance().getCurrentUser() != null;
        getCurrentUser();
        String s = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("PEACH", "onSendMessage: " + s + " / " + modelCurrentUser.getUid());
        if (!TextUtils.isEmpty(message) && uidSender != null && uidReceiver != null) {
            chatRepository.createMessage(message, modelCurrentUser, uidReceiver);
        }
    }

    // --------------------
    // REST REQUESTS
    // --------------------
    // 4 - Get Current User from FireStore
    private void getCurrentUser() {
        FirebaseUser userValue = FirebaseAuth.getInstance().getCurrentUser();

        if (userValue != null) {
            String urlPicture = (userValue.getPhotoUrl() != null) ? userValue.getPhotoUrl().toString() : null;
            String username = userValue.getDisplayName();
            String uid = userValue.getUid();

            modelCurrentUser = new User(uid, username, urlPicture);
        }
    }
}
