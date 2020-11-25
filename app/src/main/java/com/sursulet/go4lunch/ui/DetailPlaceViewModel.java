package com.sursulet.go4lunch.ui;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.R;
import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;
import com.sursulet.go4lunch.repository.DetailPlaceRepository;

public class DetailPlaceViewModel extends ViewModel {

    private final DetailPlaceRepository detailPlaceRepository;

    LiveData<DetailPlaceUiModel> detailPlaceUiModelLiveData;

    public DetailPlaceViewModel(DetailPlaceRepository detailPlaceRepository) {
        this.detailPlaceRepository = detailPlaceRepository;
    }

    public void startDetailPlace(String id) {
        detailPlaceUiModelLiveData = Transformations.map(detailPlaceRepository.getDetailPlace(id),
                new Function<GooglePlacesDetailResult, DetailPlaceUiModel>() {
            @Override
            public DetailPlaceUiModel apply(GooglePlacesDetailResult result) {

                return new DetailPlaceUiModel(
                        null,//result.getPlaceDetailResult().getName(),
                        null,//getPhotoOfPlace(result.getPlaceDetailResult().getReference(), 1000),
                        null//result.getPlaceDetailResult().getFormattedAddress()
                );
            }
        });
    }

    public LiveData<DetailPlaceUiModel> getDetailPlaceUiModelLiveData() {
        return detailPlaceUiModelLiveData;
    }

    private String getPhotoOfPlace(String reference, int maxWitch) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo");
        url.append("?maxwidth="+maxWitch);
        url.append("&photoreference="+reference);
        url.append("&key="+"AIzaSyDvUeXTbuq87mNoavyfSj_1AWVOK_dMyiE");

        return url.toString();
    }
}
