package kz.topsecurity.top_signal.service.trackingService.interfaces;


import kz.topsecurity.top_signal.service.trackingService.model.DeviceData;

public interface TrackingServiceBroadcastReceiverListener {
    void onDataReceived(DeviceData data);
}
