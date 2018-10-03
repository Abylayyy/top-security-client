package kz.topsecurity.client.helper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MapHelper {
    public static  String convertLatLngToString(LatLng latLng){
        return String.format("%f,%f",latLng.latitude, latLng.longitude);
    }

    public static LatLng convertStringLatLng(String lat, String lng){
        if(lat!=null && lng!=null && !lat.isEmpty() && !lng.isEmpty()) {
            double latutude = Double.parseDouble(lat);
            double longitude = Double.parseDouble(lng);
            return new LatLng(latutude, longitude);
        }
        return null;
    }

    public static LatLng convertDoubleLatLng(double lat, double lng){
        return new LatLng(lat, lng);
    }



    private static List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    private static double rad(double value) {
        return value * Math.PI / 180;
    };

//    public double getDistance(String p1, String p2) {
//        return getDistance(convertStringLatLng(p1),convertStringLatLng(p2));
//    };

    public static double getDistance(LatLng p1, LatLng p2) {
        int R = 6378137; // Earth’s mean radius in meter
        double dLat = rad(p2.latitude - p1.latitude);
        double dLong = rad(p2.longitude - p1.longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(rad(p1.latitude)) * Math.cos(rad(p2.latitude)) *
                        Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d; // returns the distance in meter
    };

    public static double getDistance(String position1_lat ,String position1_lng , String position2_lat ,String position2_lng) {
       LatLng p1 = convertStringLatLng(position1_lat,position1_lng);
       LatLng p2 = convertStringLatLng(position2_lat, position2_lng);
       return getDistance(p1,p2);
    };
}
