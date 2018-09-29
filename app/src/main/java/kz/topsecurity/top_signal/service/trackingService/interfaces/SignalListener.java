package kz.topsecurity.top_signal.service.trackingService.interfaces;


import kz.topsecurity.top_signal.service.trackingService.model.CellTower;

public interface SignalListener {
    void onSignalChanged(int signalStregth);
    void onCellTowerDataChanged(CellTower data);
}
