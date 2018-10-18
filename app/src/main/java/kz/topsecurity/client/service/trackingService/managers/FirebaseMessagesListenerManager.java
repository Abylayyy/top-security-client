package kz.topsecurity.client.service.trackingService.managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import kz.topsecurity.client.model.alert.Alert;
import kz.topsecurity.client.model.order.Order;
import kz.topsecurity.client.service.firebaseMessagingService.MyFirebaseMessagingService;

public class FirebaseMessagesListenerManager {
    private static final String TAG = FirebaseMessagesListenerManager.class.getSimpleName();
    private FirebaseMessageListener listener;
    private boolean blockRegister = false;
    public FirebaseMessagesListenerManager(FirebaseMessageListener listener){
        this.listener = listener;
    }

    private  boolean isFirebaseMessagesReceiverRegistered = false;

    private BroadcastReceiver mFirebaseMessagesReceiver;

    public void setupFirebaseMessagesListenerManager(Context context){
        if(!isFirebaseMessagesReceiverRegistered ){
            mFirebaseMessagesReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG,"Message Received");
                    String cloudMessageType = intent.getStringExtra(MyFirebaseMessagingService.EXTRA_TYPE_MESSAGE_KEY);
                    String cloudMessage = intent.getStringExtra(MyFirebaseMessagingService.EXTRA_MESSAGE_KEY);
                    if (cloudMessage == null) return;
                    if(cloudMessageType.equals("alert"))
                        listener.onOrderChanged(1,null);
                    else {
                        Order order = new Gson().fromJson(cloudMessage, Order.class);
                        listener.onOrderChanged(2,order);
                    }
                }
            };
            LocalBroadcastManager.getInstance(context).registerReceiver(mFirebaseMessagesReceiver,
                    new IntentFilter(MyFirebaseMessagingService.FILTER_FCM_MESSAGES_SERVICE_BROADCAST));
            isFirebaseMessagesReceiverRegistered = true;
        }
    }

    public void stop(Context context){
        if(mFirebaseMessagesReceiver!=null) {
            try {
                context.unregisterReceiver(mFirebaseMessagesReceiver);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        isFirebaseMessagesReceiverRegistered = false;
    }

    public boolean isActive(){
        if(mFirebaseMessagesReceiver == null)
            return false;
        return isFirebaseMessagesReceiverRegistered;
    }
}
