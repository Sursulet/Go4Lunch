package com.sursulet.go4lunch;

import com.sursulet.go4lunch.model.details.Close;
import com.sursulet.go4lunch.model.details.Open;
import com.sursulet.go4lunch.model.details.OpeningHours;
import com.sursulet.go4lunch.model.details.Period;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Utils {

    public static String getPhotoOfPlace(String reference, int maxWidth) {
        return "https://maps.googleapis.com/maps/api/place/photo" +
                "?maxwidth=" + maxWidth +
                "&photoreference=" + reference +
                "&key=" + ""; //TODO:KEY
    }

    public static Float getRating(double rating) {
        return (float) (3 * rating / 5);
    }

    public static String getDistance(double lat1, double lng1, double lat2, double lng2) {
        //Calculate longitude difference
        double lngDiff = lng1 - lng2;
        //Calculate distance
        double distance = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(lngDiff));

        distance = Math.acos(distance);
        //Convert distance radiant to degree
        distance = rad2deg(distance);
        //Distance in miles
        distance = distance * 60 * 1.1515;
        //Distance in kilometers in meters
        distance = distance * 1.609344 * 1000;
        //Distance arroundi
        long way = Math.round(distance);

        return way + "m";
    }

    private static double rad2deg(double distance) {
        return (distance * 180.0 / Math.PI);
    }

    //Convert degree to radian
    private static double deg2rad(double lat1) {
        return (lat1*Math.PI/180.0);
    }

    public static String getOpeningHours(OpeningHours openingHours) {
        if (openingHours == null) {
            return "unknow";
        } else if (openingHours.getOpenNow()) {
            ZoneId zone = ZoneId.of("Europe/Paris");
            LocalDate date = LocalDate.now();
            LocalTime time = LocalTime.now(zone);

            int day = getDayNumberNew(date);

            List<Period> periods = openingHours.getPeriods();
            for (int i = 0; i < periods.size(); i++) {

                Period period = periods.get(i);
                if (period.getOpen().getDay() == day) {
                    Open open = period.getOpen();
                    Close close = period.getClose();

                    LocalTime openTime = getStringTimeNew(open.getTime());
                    LocalTime closeTime = getStringTimeNew(close.getTime());

                    // Open 24/7
                    if (open.getTime().equals("0000") && close.getTime() == null) {
                        return "Open 24/7";
                    } else if (time.isAfter(openTime) && time.isBefore(closeTime)) {
                        if (time.isAfter(closeTime.minusMinutes(31))) {
                            return "Closing soon ";
                        }
                        return "Open until " + closeTime.toString();
                    }
                }
            }
        }

        return "Close";
    }

    //Days 0-6. 0 is Sunday
    private static int getDayNumberNew(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day.getValue() % 7;
    }

    private static LocalTime getStringTimeNew(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
        return LocalTime.parse(str, formatter);
    }

    /*
    private static LocalDate getStringDayNew(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(str, formatter);
    }

    public static String getDayStringNew(LocalDate date, Locale locale) {
        DayOfWeek day = date.getDayOfWeek();
        return day.getDisplayName(TextStyle.FULL, locale);
    }

     */
}
