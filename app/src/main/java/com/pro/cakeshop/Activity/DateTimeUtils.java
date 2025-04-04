package com.pro.cakeshop.Activity;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {

    public static String convertTimeStampToDate(long timestamp) {
        try {
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    // Add more date formatting methods as needed
}
