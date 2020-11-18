package com.sursulet.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.injection.ViewModelFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailPlaceActivity extends AppCompatActivity {

    ImageView photo;
    TextView name;
    TextView address;
    RatingBar rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_place);

        photo = findViewById(R.id.place_photo);
        name = findViewById(R.id.place_name);

        final DetailPlaceViewModel placeViewModel =
                new ViewModelProvider(this, ViewModelFactory.getInstance())
                .get(DetailPlaceViewModel.class);

        placeViewModel.getDetailPlaceUiModelLiveData().observe(this, new Observer<DetailPlaceUiModel>() {
            @Override
            public void onChanged(DetailPlaceUiModel detailPlaceUiModel) {
                name.setText(detailPlaceUiModel.getName());
            }
        });

    }

    private String getPhotoOfPlace(String reference, int maxWitch) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth="+maxWitch);
        url.append("&photoreference="+reference);
        url.append("&key="+getResources().getString(R.string.google_api_key));

        return url.toString();
    }
}