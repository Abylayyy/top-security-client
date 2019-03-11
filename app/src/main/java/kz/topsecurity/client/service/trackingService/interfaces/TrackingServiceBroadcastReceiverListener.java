package kz.topsecurity.client.service.trackingService.interfaces;


import kz.topsecurity.client.service.trackingService.model.DeviceData;

public interface TrackingServiceBroadcastReceiverListener {
    void onDataReceived(DeviceData data);
}
