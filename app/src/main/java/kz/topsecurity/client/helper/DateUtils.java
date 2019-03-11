package kz.topsecurity.client.helper;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    public static final int FULL_DATE_WITHOUT_TIME = 169;

    public static String convertTimeStampToReadableDate(String unix_timestamp, int type) {
        if(type == FULL_DATE_WITHOUT_TIME)
            return convertTimeStampToReadableDate(unix_timestamp,"dd.MM.yy");
        else
            return convertTimeStampToReadableDate(unix_timestamp,"HH:mm");
    }

    public static String convertTimeStampToReadableDate(String unix_timestamp) {
        return convertTimeStampToReadableDate(unix_timestamp,"HH:mm");
    }

    public static String convertTimeStampToReadableDate(String unix_timestamp,String format) {
        String timestamp = checkFormat(unix_timestamp);
        if(timestamp==null)
            return "";
        long time = Integer.valueOf(timestamp)* 1000L;

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        TimeZone tz = TimeZone.getTimeZone("GMT+6");
        cal.setTimeZone(tz);
        cal.setTimeInMillis(time);
        return DateFormat.format(format, cal).toString();
    }

    private static String checkFormat(String unix_timestamp) {
        if(unix_timestamp.length()<10)
            return null;
        if(unix_timestamp.length()==10)
            return unix_timestamp;
        else
            return correctTimeStampLength(unix_timestamp);
    }

    private static String correctTimeStampLength(String unix_timestamp) {
        return unix_timestamp.substring(0,10);
    }
}
