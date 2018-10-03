package kz.topsecurity.client.service.trackingService;

import android.content.Intent;


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
}
