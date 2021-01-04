package com.sursulet.go4lunch;

import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.sursulet.go4lunch.model.details.Close;
import com.sursulet.go4lunch.model.details.Open;
import com.sursulet.go4lunch.model.details.OpeningHours;
import com.sursulet.go4lunch.model.details.Period;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Utils {

    public static String getPhotoOfPlace(String reference, int maxWidth) {
        return "https://maps.googleapis.com/maps/api/place/photo" +
                "?maxwidth=" + maxWidth +
                "&photoreference=" + reference +
                "&key=" + ""; //TODO:KEY
    }

    public static String getRating(double rating) {
        return String.valueOf((3 * rating / 5));
    }

    public static String getDistance(double lat1, double lng1, double lat2, double lng2) {
        /*double theta = lng1 - lng2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515 * 1.609344 * 1000;

        return String.valueOf(dist);*/

        float[] results = new float[10];
        Location.distanceBetween(lat1,lng1,lat2,lng2,results);
        float distance =  results[0];
        DecimalFormat df = new DecimalFormat("###.#");
        String distanceString = df.format(distance);
        return String.valueOf(distanceString);
    }

    //TODO : Opening Hours
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getOpeningHours(OpeningHours openingHours) {
        if (openingHours == null) {
            return "unknow";
        } else {
            if (openingHours.getOpenNow()) {
                LocalDate date = LocalDate.now();
                LocalTime time = LocalTime.now();

                int day = getDayNumberNew(date);
                List<Period> periods = openingHours.getPeriods();
                Period period = periods.get(day);

                Open open = periods.get(day).getOpen();
                Close close = periods.get(day).getClose();

                LocalTime openTime = getStringDayNew(open.getTime());
                LocalTime closeTime = getStringDayNew(close.getTime());

                if(open.getDay() == 0 && open.getTime().equals("0000")) {
                    return "Open 24/7";
                } else if(time.isBefore(closeTime.minusMinutes(30))) {
                    return "Closing soon ";
                } else if(time.isAfter(openTime) && time.isBefore(closeTime)) {
                    return "Open until " + closeTime.toString();
                }
            } else {
                return "Close";
            }
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static int getDayNumberNew(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day.getValue() - 1;
    }

    private static LocalTime getStringDayNew(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        return LocalTime.parse(str, formatter);
    }

    /*
    public static String getDayStringNew(LocalDate date, Locale locale) {
        DayOfWeek day = date.getDayOfWeek();
        return day.getDisplayName(TextStyle.FULL, locale);
    }

     */
}
