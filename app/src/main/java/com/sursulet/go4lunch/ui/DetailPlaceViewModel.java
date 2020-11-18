package com.sursulet.go4lunch.ui;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;
import com.sursulet.go4lunch.repository.DetailPlaceRepository;

public class DetailPlaceViewModel extends ViewModel {

    private final DetailPlaceRepository detailPlaceRepository;
    String id;

    public DetailPlaceViewModel(DetailPlaceRepository detailPlaceRepository) {
        this.detailPlaceRepository = detailPlaceRepository;
    }

    public LiveData<DetailPlaceUiModel> getDetailPlaceUiModelLiveData() {
        return Transformations.map(detailPlaceRepository.getDetailPlace(id), new Function<GooglePlacesDetailResult, DetailPlaceUiModel>() {
            @Override
            public DetailPlaceUiModel apply(GooglePlacesDetailResult input) {

                return new DetailPlaceUiModel(
                        input.getPlaceDetailResult().getName(),
                        input.getPlaceDetailResult().getFormattedAddress()
                );
            }
        });
    }
}
