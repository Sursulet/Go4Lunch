package com.sursulet.go4lunch.utils;

import com.sursulet.go4lunch.model.details.Close;
import com.sursulet.go4lunch.model.details.Open;
import com.sursulet.go4lunch.model.details.OpeningHours;
import com.sursulet.go4lunch.model.details.Period;
import com.sursulet.go4lunch.model.details.Photo;
import com.sursulet.go4lunch.model.details.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetailTestUtils {
    public static Result buildDetailResult(
            String formattedAddress, String formattedPhoneNumber,
            String name, boolean openNew, String photo_reference, String placeId, double rating,
            String reference, String vicinity, String website
    ) {
        Result detailResult = new Result();
        List<String> weekdayText = Arrays.asList(
                "Monday: Closed",
                "Tuesday: Closed",
                "Wednesday: 12:00 – 2:00 PM, 7:00 – 9:30 PM",
                "Thursday: 12:00 – 2:00 PM, 7:00 – 9:30 PM",
                "Friday: 12:00 – 2:00 PM, 7:00 – 9:30 PM",
                "Saturday: 12:00 – 2:00 PM, 7:00 – 9:30 PM",
                "Sunday: 12:00 – 2:00 PM, 7:00 – 9:30 PM");

        List<Photo> photos = getPhotos(photo_reference);
        OpeningHours openingHours = buildOpeningHours(openNew, weekdayText);

        detailResult.setFormattedAddress(formattedAddress);
        detailResult.setFormattedPhoneNumber(formattedPhoneNumber);
        detailResult.setName(name);
        detailResult.setOpeningHours(openingHours);
        detailResult.setPhotos(photos);
        detailResult.setPlaceId(placeId);
        detailResult.setRating(rating);
        detailResult.setReference(reference);
        detailResult.setVicinity(vicinity);
        detailResult.setWebsite(website);

        return detailResult;
    }

    public static List<Photo> getPhotos(String reference) {
        List<Photo> photos = new ArrayList<>();
        Photo photo = new Photo();
        photo.setPhotoReference(reference);
        photos.add(photo);
        return photos;
    }

    private static OpeningHours buildOpeningHours(boolean openNow, List<String> weekdayText) {
        OpeningHours openingHours = new OpeningHours();
        List<Period> periods = new ArrayList<>();

        //if(!openNow) return null;

        Period period0A;
        Period period0B;
        Period period3A;
        Period period3B;
        Period period4A;
        Period period4B;
        Period period5A;
        Period period5B;
        Period period6A;
        Period period6B;

        int day0 = 0;
        period0A = initPeriod(day0, "1400", "1200");
        period0B = initPeriod(day0, "2130", "1900");

        int day3 = 3;
        period3A = initPeriod(day3, "1400", "1200");
        period3B = initPeriod(day3, "2130", "1900");

        int day4 = 4;
        period4A = initPeriod(day4, "1400", "1200");
        period4B = initPeriod(day4, "2130", "1900");

        int day5 = 5;
        period5A = initPeriod(day5, "1400", "1200");
        period5B = initPeriod(day5, "2130", "1900");

        int day6 = 6;
        period6A = initPeriod(day6, "1400", "1200");
        period6B = initPeriod(day6, "2130", "1900");

        periods.add(period0A);
        periods.add(period0B);
        periods.add(period3A);
        periods.add(period3B);
        periods.add(period4A);
        periods.add(period4B);
        periods.add(period5A);
        periods.add(period5B);
        periods.add(period6A);
        periods.add(period6B);

        openingHours.setOpenNow(openNow);
        openingHours.setPeriods(periods);
        openingHours.setWeekdayText(weekdayText);

        return openingHours;
    }

    private static Period initPeriod(int day, String close, String open) {
        Period period = new Period();
        period.setClose(initClose(day, close));
        period.setOpen(initOpen(day, open));
        return period;
    }

    private static Close initClose(int day, String time) {
        Close close = new Close();
        close.setDay(day);
        close.setTime(time);
        return close;
    }

    private static Open initOpen(int day, String time) {
        Open open = new Open();
        open.setDay(day);
        open.setTime(time);
        return open;
    }
}
