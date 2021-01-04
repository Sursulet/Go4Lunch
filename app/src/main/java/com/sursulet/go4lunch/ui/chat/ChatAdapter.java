package com.sursulet.go4lunch.ui.chat;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.bumptech.glide.RequestManager;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.model.Message;

//TODO :
public class ChatAdapter extends ListAdapter<Message, MessageViewHolder> {

    public interface Listener {
        void onDataChanged();
    }

    //FOR DATA
    private final RequestManager glide;
    private final String idCurrentUser;

    //FOR COMMUNICATION
    private final Listener callback;

    public ChatAdapter(
            @NonNull DiffUtil.ItemCallback<Message> diffCallback,
            RequestManager glide,
            Listener callback,
            String idCurrentUser
    ) {
        super(diffCallback);
        this.glide = glide;
        this.callback = callback;
        this.idCurrentUser = idCurrentUser;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message model = getItem(position);
        holder.updateWithMessage(model, this.idCurrentUser, this.glide);
    }

    public void onDataChanged() {
        //super.onDataChanged();
        this.callback.onDataChanged();
    }

    public static final DiffUtil.ItemCallback<Message> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Message>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Message oldMessage, @NonNull Message newMessage) {
                    return oldMessage.getMessage().equals(newMessage.getMessage());
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull Message oldMessage, @NonNull Message newMessage) {
                    return oldMessage.getMessage().equals(newMessage.getMessage());
                }
            };
}
