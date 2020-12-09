package com.sursulet.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.api.UserHelper;
import com.sursulet.go4lunch.injection.ViewModelFactory;
import com.sursulet.go4lunch.ui.workmates.WorkmatesAdapter;
import com.sursulet.go4lunch.ui.workmates.WorkmatesUiModel;

import java.util.List;

public class DetailPlaceActivity extends AppCompatActivity {

    ImageView photo;
    TextView name;
    TextView address;
    RatingBar rating;
    TextView likeBtn;

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
        likeBtn = findViewById(R.id.detail_like_btn);

        final DetailPlaceViewModel placeViewModel =
                new ViewModelProvider(this, ViewModelFactory.getInstance())
                        .get(DetailPlaceViewModel.class);

        placeViewModel.startDetailPlace(id);
        placeViewModel.getDetailPlaceUiModelLiveData().observe(this, new Observer<DetailPlaceUiModel>() {
            @Override
            public void onChanged(DetailPlaceUiModel detailPlaceUiModel) {
                name.setText(detailPlaceUiModel.getName());
                Log.d("PEACH", "onChanged: "+detailPlaceUiModel.getUrlPhoto());

                Glide.with(photo)
                        .load(detailPlaceUiModel.getUrlPhoto())
                        .into(photo);

                address.setText(detailPlaceUiModel.getTxt());
            }
        });

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailPlaceActivity.this, "LIKE", Toast.LENGTH_SHORT).show();
                placeViewModel.onLikeButtonClick();
            }
        });
/*
        RecyclerView recyclerView = findViewById(R.id.detail_workmates);
        recyclerView.addItemDecoration(new DividerItemDecoration(DetailPlaceActivity.this, DividerItemDecoration.VERTICAL));
        WorkmatesAdapter adapter = new WorkmatesAdapter(WorkmatesUiModel.DIFF_CALLBACK);
        recyclerView.setAdapter(adapter);

        placeViewModel.getWorkmatesUiModelLiveData().observe(this, new Observer<List<WorkmatesUiModel>>() {
            @Override
            public void onChanged(List<WorkmatesUiModel> workmatesUiModels) {
                adapter.submitList(workmatesUiModels);
            }
        });*/

    }

    private String getPhotoOfPlace(String reference, int maxWitch) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth=" + maxWitch);
        url.append("&photoreference=" + reference);
        url.append("&key=" + getResources().getString(R.string.google_api_key));

        return url.toString();
    }
}