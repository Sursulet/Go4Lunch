package com.sursulet.go4lunch.ui.chat;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.api.MessageHelper;
import com.sursulet.go4lunch.model.Message;
import com.sursulet.go4lunch.repository.ChatRepository;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;

    MutableLiveData<List<Message>> uiModelMutableLiveData = new MutableLiveData<>();

    public ChatViewModel(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    LiveData<List<Message>> getUiModelMutableLiveData() {
        return uiModelMutableLiveData;
    }

    public void getUser() {
        chatRepository.getCurrentUserFromFirestore();
    }

    public void getMessages() {
        chatRepository.getMessages();
    }

    public void onSendMessage(String message) {
        /*
        if(!TextUtils.isEmpty(message) && modelCurrentUser != null) {
            MessageHelper.createMessageForChat(message, modelCurrentUser).addOnFailureListener(this.onFailureListener());
        }

         */
    }
}
