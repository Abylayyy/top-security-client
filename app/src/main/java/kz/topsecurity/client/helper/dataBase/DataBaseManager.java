package kz.topsecurity.client.helper.dataBase;

import android.content.Context;

import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.model.other.Healthcard;
import kz.topsecurity.client.service.trackingService.model.DeviceData;

public interface DataBaseManager {
    void saveDeviceData(DeviceData deviceData);
    DeviceData getDeviceData();
    void dropDeviceDataTable();

    void saveClientData(Client data);
    Client getClientData();
    void dropClientData();

    void updateHealthCard(Healthcard healthcard, Context context);

    void updateDatabase(String reason);
    void updateClientPhoto(String url);
}