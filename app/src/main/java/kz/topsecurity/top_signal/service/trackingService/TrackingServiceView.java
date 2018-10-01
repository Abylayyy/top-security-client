package kz.topsecurity.top_signal.service.trackingService;

import android.content.Intent;


import kz.topsecurity.top_signal.service.trackingService.model.DeviceData;

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
