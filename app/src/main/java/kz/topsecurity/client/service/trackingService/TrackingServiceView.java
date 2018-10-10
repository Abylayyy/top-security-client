package kz.topsecurity.client.service.trackingService;

import android.content.Intent;


import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;

import kz.topsecurity.client.service.trackingService.model.DeviceData;

public interface TrackingServiceView {
    void onDataPrepared(Intent intent);
    void onDataPrepared(DeviceData deviceData);
    void onShowToast(int no_devices);
    void onBroadcastMessage(int actionStatusAlertSend);
    void onShowToast(String s);
    void setAlertSendStatus();
    void setAlertFailedStatus();
    void setAlertCancelFailedStatus();
    void setAlertCanceledStatus();

    void onLocationNotAvailable();

    void checkLocationRequest(LocationRequest locationRequest);
}
