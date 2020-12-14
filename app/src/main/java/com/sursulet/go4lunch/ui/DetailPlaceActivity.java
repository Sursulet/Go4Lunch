package com.sursulet.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.injection.ViewModelFactory;
import com.sursulet.go4lunch.ui.workmates.WorkmatesAdapter;
import com.sursulet.go4lunch.ui.workmates.WorkmatesUiModel;

public class DetailPlaceActivity extends AppCompatActivity {

    ImageView photo;
    TextView name;
    TextView address;
    RatingBar rating;
    TextView callBtn;
    TextView likeBtn;
    TextView websiteBtn;
    FloatingActionButton fab;

    boolean isLike;

    public static Intent getStartIntent(Context context, String id) {
        Intent intent = new Intent(context, DetailPlaceActivity.class);
        intent.putExtra("id", id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_place);

        Intent i = getIntent();
        String id = i.getStringExtra("id");

        photo = findViewById(R.id.detail_photo);
        name = findViewById(R.id.detail_name);
        address = findViewById(R.id.detail_address);
        callBtn = findViewById(R.id.detail_call_btn);
        likeBtn = findViewById(R.id.detail_like_btn);
        websiteBtn = findViewById(R.id.detail_website_btn);
        fab = findViewById(R.id.detail_fab);

        final DetailPlaceViewModel placeViewModel =
                new ViewModelProvider(this, ViewModelFactory.getInstance())
                        .get(DetailPlaceViewModel.class);

        placeViewModel.startDetailPlace(id);
        placeViewModel.getUiModelLiveData().observe(this, new Observer<DetailPlaceUiModel>() {
            @Override
            public void onChanged(DetailPlaceUiModel detailPlaceUiModel) {
                name.setText(detailPlaceUiModel.getName());

                Glide.with(photo)
                        .load(detailPlaceUiModel.getUrlPhoto())
                        .into(photo);

                address.setText(detailPlaceUiModel.getTxt());

                RecyclerView recyclerView = findViewById(R.id.detail_workmates);
                recyclerView.addItemDecoration(new DividerItemDecoration(DetailPlaceActivity.this, DividerItemDecoration.VERTICAL));
                WorkmatesAdapter adapter = new WorkmatesAdapter(WorkmatesUiModel.DIFF_CALLBACK);
                recyclerView.setAdapter(adapter);
                adapter.submitList(detailPlaceUiModel.getWorkmates());
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailPlaceActivity.this, "GOING", Toast.LENGTH_SHORT).show();
                placeViewModel.onGoingButtonClick();
            }
        });

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailPlaceActivity.this, "LIKE", Toast.LENGTH_SHORT).show();
                placeViewModel.onLikeButtonClick();
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onMakeCall();
            }
        });

        websiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

    }
}