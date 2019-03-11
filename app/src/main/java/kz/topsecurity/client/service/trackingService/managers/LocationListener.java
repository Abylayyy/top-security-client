package kz.topsecurity.client.service.trackingService.managers;

import com.google.android.gms.location.LocationRequest;

public interface LocationListener {
    void onLocationChanged(Double lat, Double lng, Double alt, String street);
    void onLocationRequestCheck(LocationRequest locationRequest);
}
