package com.sursulet.go4lunch.ui.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.injection.ViewModelFactory;
import com.sursulet.go4lunch.model.User;

public class ChatActivity extends AppCompatActivity {

    //private static final String TAG = ChatActivity.class.getSimpleName();

    ImageView userAvatar;
    TextView username;

    RecyclerView recyclerView;
    TextInputEditText editTextMessage;

    ImageButton sendBtn;

    ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;

    public static Intent getStartIntent(Context context, String id) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("id", id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent i = getIntent();
        String id = i.getStringExtra("id");

        userAvatar = findViewById(R.id.chat_toolbar_img);
        username = findViewById(R.id.chat_toolbar_title);
        recyclerView = findViewById(R.id.activity_chat_recycler_view);
        editTextMessage = findViewById(R.id.activity_chat_message_edit_text);
        sendBtn = findViewById(R.id.activity_chat_send_button);

        chatViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ChatViewModel.class);
        chatViewModel.setUid(id);

        this.configureToolbar();
        this.configureRecyclerView();
        chatViewModel.getUserReceiver().observe(this, this::updateUIProfileUserReceiver);

        chatViewModel.getUiModelMutableLiveData().observe(this, messages -> {
            Log.d("PEACH", "onCreate: " + messages.size());
            chatAdapter.submitList(messages);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.smoothScrollToPosition(messages.size());
                }
            }, 500);

        });

        sendBtn.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(editTextMessage.getText())) {
                chatViewModel.onSendMessage(editTextMessage.getText().toString());
            } else {
                Toast.makeText(ChatActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
            }
            editTextMessage.setText("");
        });
    }

    private void updateUIProfileUserReceiver(User user) {
        username.setText(user.getUsername());
        Glide.with(userAvatar)
                .load(user.getAvatarUrl())
                .circleCrop()
                .into(userAvatar);
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void configureRecyclerView(){
        this.chatAdapter = new ChatAdapter(
                MessageUiModel.DIFF_CALLBACK,
                ChatActivity.this,
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        );

        recyclerView.setAdapter(this.chatAdapter);
    }

}