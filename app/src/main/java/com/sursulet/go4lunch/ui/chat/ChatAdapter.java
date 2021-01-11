package com.sursulet.go4lunch.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sursulet.go4lunch.R;

//TODO : Faut-il utiliser ListAdapter ?
public class ChatAdapter extends ListAdapter<MessageUiModel, ChatAdapter.MessageViewHolder> {

    //FOR DATA
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private final String idCurrentUser;

    //FOR COMMUNICATION

    public ChatAdapter(
            @NonNull DiffUtil.ItemCallback<MessageUiModel> diffCallback,
            String idCurrentUser
    ) {
        super(diffCallback);
        this.idCurrentUser = idCurrentUser;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_right, parent, false));
        } else {
            return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_left, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageUiModel messageUiModel = getItem(position);
        holder.bind(messageUiModel);
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position).getUserSender().getUid().equals(idCurrentUser)){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout item;
        TextView message;
        TextView date;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.chat_item_root_view);
            message = itemView.findViewById(R.id.chat_item_message);
            date = itemView.findViewById(R.id.chat_item_message_date);
        }

        public void bind(MessageUiModel messageUiModel) {
            message.setText(messageUiModel.getMessage());
            date.setText(messageUiModel.getDateCreated());
        }
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
                    return oldMessage.getMessage().equals(newMessage.getMessage());
                }
            };
}
