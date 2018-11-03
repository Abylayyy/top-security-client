package kz.topsecurity.client.service.trackingService.managers;


import kz.topsecurity.client.model.alert.Alert;
import kz.topsecurity.client.model.order.Order;

public interface FirebaseMessageListener {
    void onOrderChanged(String type );
}
