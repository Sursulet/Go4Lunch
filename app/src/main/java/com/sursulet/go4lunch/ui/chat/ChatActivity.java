package com.sursulet.go4lunch.ui.chat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.api.UserHelper;
import com.sursulet.go4lunch.injection.ViewModelFactory;
import com.sursulet.go4lunch.model.Message;
import com.sursulet.go4lunch.model.User;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity implements ChatAdapter.Listener {

    // FOR DESIGN
    // 1 - Getting all views needed
    @BindView(R.id.activity_chat_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.activity_chat_text_view_recycler_view_empty) TextView textViewRecyclerViewEmpty;
    @BindView(R.id.activity_chat_message_edit_text) TextInputEditText editTextMessage;
    @BindView(R.id.activity_chat_image_chosen_preview) ImageView imageViewPreview;

    // FOR DATA
    // 2 - Declaring Adapter and data
    ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;
    @Nullable private User modelCurrentUser;
    private String currentChatName;

    // STATIC DATA FOR CHAT (3)
    private static final String CHAT_NAME_ANDROID = "android";
    private static final String CHAT_NAME_BUG = "bug";
    private static final String CHAT_NAME_FIREBASE = "firebase";

    public static Intent getStartIntent(Context context, String id) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("id", id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String id = i.getStringExtra("id");

        chatViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ChatViewModel.class);

        this.configureRecyclerView(CHAT_NAME_ANDROID);
        //this.configureToolbar();
        chatViewModel.getUser();
    }

    //@Override
    public int getFragmentLayout() { return R.layout.activity_chat; }

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.activity_chat_send_button)
    public void onClickSendMessage() {
        chatViewModel.onSendMessage("");
        editTextMessage.setText("");
    }

    /*
    @OnClick({ R.id.activity_chat_android_chat_button, R.id.activity_chat_firebase_chat_button, R.id.activity_chat_bug_chat_button})
    public void onClickChatButtons(ImageButton imageButton) {
        // 8 - Re-Configure the RecyclerView depending chosen chat
        switch (Integer.valueOf(imageButton.getTag().toString())){
            case 10:
                this.configureRecyclerView(CHAT_NAME_ANDROID);
                break;
            case 20:
                this.configureRecyclerView(CHAT_NAME_FIREBASE);
                break;
            case 30:
                this.configureRecyclerView(CHAT_NAME_BUG);
                break;
        }
    }*/

    @OnClick(R.id.activity_chat_add_file_button)
    public void onClickAddFile() { }

    // --------------------
    // REST REQUESTS
    // --------------------
    // 4 - Get Current User from Firestore
    private void getCurrentUserFromFirestore(){
        UserHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUser = documentSnapshot.toObject(User.class);
            }
        });
    }

    // --------------------
    // UI
    // --------------------
    // 5 - Configure RecyclerView with a Query
    private void configureRecyclerView(String chatName){
        //Track current chat name
        this.currentChatName = chatName;
        //Configure Adapter & RecyclerView
        this.chatAdapter = new ChatAdapter(
                ChatAdapter.DIFF_CALLBACK,
                //generateOptionsForAdapter(),
                Glide.with(this), this, FirebaseAuth.getInstance().getCurrentUser().getUid()
        );

        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.chatAdapter);

        chatViewModel.getUiModelMutableLiveData().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                chatAdapter.submitList(messages);
            }
        });
    }

    // 6 - Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onDataChanged() {
        // 7 - Show TextView in case RecyclerView is empty
        textViewRecyclerViewEmpty.setVisibility(this.chatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}