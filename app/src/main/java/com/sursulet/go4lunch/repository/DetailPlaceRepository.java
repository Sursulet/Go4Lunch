package com.sursulet.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sursulet.go4lunch.model.details.Close;
import com.sursulet.go4lunch.model.details.DetailResult;
import com.sursulet.go4lunch.model.details.GooglePlacesDetailResult;
import com.sursulet.go4lunch.model.details.Open;
import com.sursulet.go4lunch.model.details.OpeningHours;
import com.sursulet.go4lunch.model.details.Period;
import com.sursulet.go4lunch.remote.IGoogleAPIService;
import com.sursulet.go4lunch.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPlaceRepository {

    IGoogleAPIService mService = RetrofitClient
            .getClient("https://maps.googleapis.com/")
            .create(IGoogleAPIService.class);

    public LiveData<GooglePlacesDetailResult> getDetailPlace(String place_id) {

        MutableLiveData<GooglePlacesDetailResult> placeMutableLiveData = new MutableLiveData<>();

        mService.getDetailPlace(
                place_id,
                "" //TODO : KEY
        ).enqueue(new Callback<GooglePlacesDetailResult>() {
            @Override
            public void onResponse(
                    Call<GooglePlacesDetailResult> call,
                    Response<GooglePlacesDetailResult> response
            ) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        placeMutableLiveData.postValue(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<GooglePlacesDetailResult> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return placeMutableLiveData;
    }

    public LiveData<GooglePlacesDetailResult> init() {
        MutableLiveData<GooglePlacesDetailResult> mutableLiveData = new MutableLiveData<>();
        DetailResult detailResult = new DetailResult();
        detailResult.setFormattedAddress("20 Rue Saint-Martin, 75004 Paris, France");
        detailResult.setFormattedPhoneNumber("01 42 72 25 76");
        detailResult.setName("Benoit Paris");
        OpeningHours openingHours = new OpeningHours();
        openingHours.setOpenNow(true);

        Period period0 = new Period();
        Close close0 = new Close();
        close0.setDay(0);
        close0.setTime("1400");
        Open open0 = new Open();
        open0.setDay(0);
        open0.setTime("1200");
        period0.setClose(close0);
        period0.setOpen(open0);

        Period period0B = new Period();
        Close close0B = new Close();
        close0B.setDay(0);
        close0B.setTime("2130");
        Open open0B = new Open();
        open0B.setDay(0);
        open0B.setTime("1900");
        period0B.setClose(close0B);
        period0B.setOpen(open0B);

        Period period3A = new Period();
        Close close3A = new Close();
        close3A.setDay(3);
        close3A.setTime("1400");
        Open open3A = new Open();
        open3A.setDay(3);
        open3A.setTime("1200");
        period3A.setClose(close3A);
        period3A.setOpen(open3A);

        Period period3B = new Period();
        Close close3B = new Close();
        close3B.setDay(3);
        close3B.setTime("2130");
        Open open3B = new Open();
        open3B.setDay(3);
        open3B.setTime("1900");
        period3B.setClose(close3B);
        period3B.setOpen(open3B);

        Period period4A = new Period();
        Close close4A = new Close();
        close4A.setDay(4);
        close4A.setTime("1400");
        Open open4A = new Open();
        open4A.setDay(4);
        open4A.setTime("1200");
        period4A.setClose(close4A);
        period4A.setOpen(open4A);

        Period period4B = new Period();
        Close close4B = new Close();
        close4B.setDay(4);
        close4B.setTime("2130");
        Open open4B = new Open();
        open4B.setDay(4);
        open4B.setTime("1900");
        period4B.setClose(close4B);
        period4B.setOpen(open4B);

        Period period5A = new Period();
        Close close5A = new Close();
        close5A.setDay(5);
        close5A.setTime("1400");
        Open open5A = new Open();
        open5A.setDay(5);
        open5A.setTime("1200");
        period5A.setClose(close5A);
        period5A.setOpen(open5A);

        Period period5B = new Period();
        Close close5B = new Close();
        close5B.setDay(5);
        close5B.setTime("2130");
        Open open5B = new Open();
        open5B.setDay(5);
        open5B.setTime("1900");
        period5B.setClose(close5B);
        period5B.setOpen(open5B);

        Period period6A = new Period();
        Close close6A = new Close();
        close6A.setDay(6);
        close6A.setTime("1400");
        Open open6A = new Open();
        open6A.setDay(6);
        open6A.setTime("1200");
        period6A.setClose(close6A);
        period6A.setOpen(open6A);

        Period period6B = new Period();
        Close close6B = new Close();
        close6B.setDay(6);
        close6B.setTime("2130");
        Open open6B = new Open();
        open6B.setDay(6);
        open6B.setTime("1900");
        period6B.setClose(close6B);
        period6B.setOpen(open6B);

        List<Period> periods = new ArrayList<>();
        periods.add(period0);
        periods.add(period0B);
        periods.add(period3A);
        periods.add(period3B);
        periods.add(period4A);
        periods.add(period4B);
        periods.add(period5A);
        periods.add(period5B);
        periods.add(period6A);
        periods.add(period6B);

        openingHours.setPeriods(periods);

        List<String> weekday = new ArrayList<>();
        weekday.add("Monday: Closed");
        weekday.add("Tuesday: Closed");
        weekday.add("Wednesday: 12:00 – 2:00 PM, 7:00 – 9:30 PM");
        weekday.add("Thursday: 12:00 – 2:00 PM, 7:00 – 9:30 PM");
        weekday.add("Friday: 12:00 – 2:00 PM, 7:00 – 9:30 PM");
        weekday.add("Saturday: 12:00 – 2:00 PM, 7:00 – 9:30 PM");
        weekday.add("Sunday: 12:00 – 2:00 PM, 7:00 – 9:30 PM");

        openingHours.setWeekdayText(weekday);
        detailResult.setOpeningHours(openingHours);

        detailResult.setPlaceId("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");
        detailResult.setPriceLevel(4);
        detailResult.setRating(4.1);
        detailResult.setVicinity("20 Rue Saint-Martin, Paris");
        detailResult.setWebsite("http://www.benoit-paris.com/");
        detailResult.setReference("ChIJQ0bNfR5u5kcR9Z0i41-E7sg");

        GooglePlacesDetailResult googlePlacesDetailResult = new GooglePlacesDetailResult();
        googlePlacesDetailResult.setDetailResult(detailResult);
        mutableLiveData.postValue(googlePlacesDetailResult);
        return mutableLiveData;
    }
}
