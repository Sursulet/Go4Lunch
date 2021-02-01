package com.sursulet.go4lunch.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.Utils;
import com.sursulet.go4lunch.model.Message;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.repository.ChatRepository;
import com.sursulet.go4lunch.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    //MutableLiveData<List<MessageUiModel>> uiModelMutableLiveData = new MutableLiveData<>();
    LiveData<List<Message>> allMessagesLiveData;
    private LiveData<User> userReceiver;
    private LiveData<User> currentUser;
    private LiveData<String> chat;

    public ChatViewModel(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public void setUid(String uidReceiver) {
        chat = chatRepository.createChatName(uidReceiver);
        allMessagesLiveData = chatRepository.getAllMessages(chat.getValue());
        currentUser = userRepository.getCurrentUser();
        userReceiver = userRepository.getUser(uidReceiver);
    }

    LiveData<User> getUserReceiver() {
        return userReceiver;
    }

    LiveData<List<MessageUiModel>> getUiModelMutableLiveData() {
        return Transformations.map(
                allMessagesLiveData,
                messages -> {
                    List<MessageUiModel> results = new ArrayList<>();
                    for (Message message : messages) {
                        String date = "";
                        if(message.getDateCreated() != null) date = Utils.convertDateToHour(message.getDateCreated());
                        MessageUiModel messageUiModel = new MessageUiModel(
                                message.getMessage(),
                                date,
                                message.getUserSender()
                        );

                        results.add(messageUiModel);
                    }

                    return results;
                });
    }

    public void onSendMessage(String message) {

        if (currentUser != null) {
            chatRepository.createMessage(chat.getValue(), message, currentUser.getValue());
        }
    }

}
