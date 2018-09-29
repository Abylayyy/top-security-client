package kz.topsecurity.top_signal.helper.dataBase;

import kz.topsecurity.top_signal.model.other.Client;
import kz.topsecurity.top_signal.service.trackingService.model.DeviceData;

public interface DataBaseManager {
    void saveDeviceData(DeviceData deviceData);
    DeviceData getDeviceData();
    void dropDeviceDataTable();

    void saveClientData(Client data);
    Client getClientData();
    void dropClientData();
}