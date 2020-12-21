package com.sursulet.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
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

public class DetailPlaceActivity extends AppCompatActivity implements OnItemClickListener {

    private static final int REQUEST_CALL = 1;
    ImageView photo;
    TextView name;
    TextView address;
    RatingBar rating;
    TextView callBtn;
    TextView likeBtn;
    TextView websiteBtn;
    FloatingActionButton fab;

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

        RecyclerView recyclerView = findViewById(R.id.detail_workmates);
        recyclerView.addItemDecoration(new DividerItemDecoration(DetailPlaceActivity.this, DividerItemDecoration.VERTICAL));
        WorkmatesAdapter adapter = new WorkmatesAdapter(WorkmatesUiModel.DIFF_CALLBACK, this);
        recyclerView.setAdapter(adapter);

        final DetailPlaceViewModel placeViewModel =
                new ViewModelProvider(this, ViewModelFactory.getInstance())
                        .get(DetailPlaceViewModel.class);

        placeViewModel.startDetailPlace(id);
        placeViewModel.getUiModelLiveData().observe(this, detailPlaceUiModel -> {
            name.setText(detailPlaceUiModel.getName());

            Glide.with(photo)
                    .load(detailPlaceUiModel.getUrlPhoto())
                    .into(photo);

            address.setText(detailPlaceUiModel.getTxt());
            //rating.setRating(Float.parseFloat(detailPlaceUiModel.getRatingBar()));
            fab.setImageTintList(ColorStateList.valueOf(detailPlaceUiModel.getIsGoing()));
            likeBtn.setBackgroundTintList(ColorStateList.valueOf(detailPlaceUiModel.getIsLike()));


            adapter.submitList(detailPlaceUiModel.getWorkmates());
        });

        fab.setOnClickListener(v -> placeViewModel.onGoingButtonClick());

        likeBtn.setOnClickListener(v -> placeViewModel.onLikeButtonClick());

        callBtn.setOnClickListener(v -> onMakePhoneCall());

        websiteBtn.setOnClickListener(v -> onGoToUrl());

    }

    //TODO : Phone call
    private void onMakePhoneCall() {
        String number = callBtn.getText().toString();
        if(number.trim().length() > 0) {
            if(ContextCompat.checkSelfPermission(DetailPlaceActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DetailPlaceActivity.this, new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel: " + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        } else {
            Toast.makeText(DetailPlaceActivity.this, "No phone number", Toast.LENGTH_SHORT).show();
        }
    }

    private void onGoToUrl() {
        String url = websiteBtn.getText().toString();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMakePhoneCall();
            } else {
                Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemClick(int position) {

    }
}