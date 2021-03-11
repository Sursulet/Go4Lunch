package com.sursulet.go4lunch.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sursulet.go4lunch.R;

public class ChatAdapter extends ListAdapter<MessageUiModel, ChatAdapter.MessageViewHolder> {

    private final Context context;
    private final String idCurrentUser;

    public ChatAdapter(
            @NonNull DiffUtil.ItemCallback<MessageUiModel> diffCallback,
            Context context,
            String idCurrentUser
    ) {
        super(diffCallback);
        this.context = context;
        this.idCurrentUser = idCurrentUser;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item_right, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageUiModel messageUiModel = getItem(position);
        holder.bind(messageUiModel, context, idCurrentUser);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        final TextView message;
        final TextView date;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_item_message);
            date = itemView.findViewById(R.id.chat_item_message_date);
        }

        public void bind(MessageUiModel messageUiModel, Context context, String idCurrentUser) {
            boolean isCurrentUser = messageUiModel.getUserSender().getUid().equals(idCurrentUser);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_END);

            if (isCurrentUser) {
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                message.setBackground(ContextCompat.getDrawable(context, R.drawable.message_left));
                message.setTextColor(ContextCompat.getColorStateList(context, R.color.white));
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_START);
                message.setBackground(ContextCompat.getDrawable(context, R.drawable.message_right));
                message.setTextColor(ContextCompat.getColorStateList(context, R.color.black));
            }

            message.setLayoutParams(params);

            message.setText(messageUiModel.getMessage());
            date.setText(messageUiModel.getDateCreated());
        }
    }
}
