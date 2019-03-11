package kz.topsecurity.client.service.trackingService.interfaces;


import kz.topsecurity.client.service.trackingService.model.CellTower;

public interface SignalListener {
    void onSignalChanged(int signalStregth);
    void onCellTowerDataChanged(CellTower data);
}
