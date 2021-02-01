package com.sursulet.go4lunch.ui.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.sursulet.go4lunch.model.User;

public class MessageUiModel {

    private final String message;
    private final String dateCreated;
    private final User userSender;

    public MessageUiModel(String message, String dateCreated, User userSender) {
        this.message = message;
        this.dateCreated = dateCreated;
        this.userSender = userSender;
    }

    public String getMessage() { return message; }
    public String getDateCreated() { return dateCreated; }
    public User getUserSender() { return userSender; }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageUiModel messageUiModel = (MessageUiModel) o;
        return message.equals(messageUiModel.message)
                && dateCreated.equals(messageUiModel.dateCreated)
                && userSender.equals(messageUiModel.userSender);
    }

    public static final DiffUtil.ItemCallback<MessageUiModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MessageUiModel>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull MessageUiModel oldMessage, @NonNull MessageUiModel newMessage) {
                    return oldMessage.getMessage().equals(newMessage.getMessage());
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull MessageUiModel oldMessage, @NonNull MessageUiModel newMessage) {
                    return oldMessage.equals(newMessage);
                }
            };
}
