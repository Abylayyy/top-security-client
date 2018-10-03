package kz.topsecurity.client.service.trackingService.managers;

public interface LocationListener {
    void onLocationChanged(Double lat, Double lng, Double alt, String street);
}
