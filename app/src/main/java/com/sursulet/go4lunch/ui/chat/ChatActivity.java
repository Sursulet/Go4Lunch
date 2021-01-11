package com.sursulet.go4lunch.ui.chat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

    // FOR DESIGN
    ImageView userAvatar;
    TextView username;

    RecyclerView recyclerView;
    //@BindView(R.id.activity_chat_text_view_recycler_view_empty) TextView textViewRecyclerViewEmpty;
    TextInputEditText editTextMessage;
    //@BindView(R.id.activity_chat_image_chosen_preview) ImageView imageViewPreview;

    ImageButton sendBtn;

    // FOR DATA
    ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;
    @Nullable private User modelCurrentUser;
    private String currentChatName;

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
        chatViewModel.init(id);

        this.configureToolbar();
        this.configureRecyclerView();
        chatViewModel.getUserReceiver().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                username.setText(user.getUsername());
                Glide.with(userAvatar)
                        .load(user.getAvatarUrl())
                        .circleCrop()
                        .into(userAvatar);
            }
        });

        chatViewModel.getUiModelMutableLiveData().observe(this, messages -> {
            Log.d("PEACH", "onCreate: ACTIVITY " + messages.get(1).getMessage());
            chatAdapter.submitList(messages);
        });

        sendBtn.setOnClickListener(v -> {
            assert editTextMessage.getText() != null;
            String msg = editTextMessage.getText().toString();
            Log.d("PEACH", "onClick: " + id + " msg : " + msg);
            if(!msg.equals("")) {
                chatViewModel.onSendMessage(msg, FirebaseAuth.getInstance().getCurrentUser().getUid(), id);
            } else {
                Toast.makeText(ChatActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
            }
            editTextMessage.setText("");
        });
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void configureRecyclerView(){
        //recyclerView.addItemDecoration(new DividerItemDecoration(ChatActivity.this, DividerItemDecoration.VERTICAL));
        this.chatAdapter = new ChatAdapter(
                ChatAdapter.DIFF_CALLBACK,
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        );
        recyclerView.setAdapter(this.chatAdapter);
    }

}