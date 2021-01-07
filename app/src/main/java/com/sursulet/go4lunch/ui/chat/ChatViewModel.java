package com.sursulet.go4lunch.ui.chat;

import android.text.TextUtils;

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

    private User modelCurrentUser;

    public ChatViewModel(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public void init(String userSender, String userReceiver) {
        allMessagesLiveData = Transformations.map(
                chatRepository.getAllMessages(userSender, userReceiver),
                messages -> {
                    List<MessageUiModel> results = new ArrayList<>();
                    for (Message message : messages) {

                        MessageUiModel messageUiModel = new MessageUiModel(
                                message.getMessage(),
                                message.getDateCreated(),
                                message.getUserSender(),
                                message.getUrlImage()
                        );

                        results.add(messageUiModel);
                    }

                    return results;
                });
    }

    LiveData<List<MessageUiModel>> getUiModelMutableLiveData() {
        return allMessagesLiveData;
    }

    public void onSendMessage(String message, String uidSender, String uidReceiver) {
        assert FirebaseAuth.getInstance().getCurrentUser() != null;
        getCurrentUser();
        if (!TextUtils.isEmpty(message) && uidSender != null && uidReceiver != null) {
            chatRepository.createMessage(message, modelCurrentUser, uidReceiver);
        }
    }

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
