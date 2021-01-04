package com.sursulet.go4lunch;

import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.sursulet.go4lunch.model.details.Open;
import com.sursulet.go4lunch.model.details.OpeningHours;
import com.sursulet.go4lunch.model.details.Period;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    private String getDistance(double lat1, double lng1, double lat2, double lng2) {
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
    public static String getOpeningHours(OpeningHours openingHours) {
        if (openingHours == null) {
            return "unknow";
        } else {
            if (openingHours.getOpenNow()) {
                int day = getDayNumberOld(Calendar.getInstance().getTime());
                Date hour = Calendar.getInstance().getTime();
                Log.d("PEACH", "getOpeningHours: " + hour.toString()); //Mon Dec 28 17:25:55 GMT+00:00 2020
                int minHour = 30;

                List<Period> periods = openingHours.getPeriods();
                Open openDay = periods.get(day).getOpen();
                Log.d("PEACH", "getOpeningHours: " + periods.get(day));
                Date openTime = getStringToHourOld(periods.get(day).getOpen().getTime());
                Date closeTime = getStringToHourOld(periods.get(day).getClose().getTime());

                // Open 24/7
                if (openDay.getDay() == 0 && openDay.getTime().equals("0000")) {
                    //openingHours.getPeriods().get(day).getClose() == null
                    return "Open 24/7";
                } else if (openTime.after(hour) && closeTime.before(hour)) {
                    if(closeTime.compareTo(hour) < minHour) { return "Closing soon"; }
                    return "Open until " + closeTime.toString();
                }
            } else {
                return "Close";
            }
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getDayNumberNew(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day.getValue() - 1;
    }

    public static int getDayNumberOld(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static int getHourNumberOld(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static Date getStringToHourOld(String strTime) {
        DateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.FRANCE);
        Date d = null;
        try {
            d = dateFormat.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return d;
    }
}
