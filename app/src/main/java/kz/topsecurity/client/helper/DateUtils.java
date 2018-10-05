package kz.topsecurity.client.helper;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    public static String convertTimeStampToReadableDate(String timestamp) {
        long time = Integer.valueOf(timestamp)* 1000L;

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        TimeZone tz = TimeZone.getTimeZone("GMT+6");
        cal.setTimeZone(tz);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("HH:mm", cal).toString();
        return date;
    }
}
