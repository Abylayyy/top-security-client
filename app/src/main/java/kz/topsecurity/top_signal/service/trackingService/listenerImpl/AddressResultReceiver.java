package kz.topsecurity.top_signal.service.trackingService.listenerImpl;

import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;


import kz.topsecurity.top_signal.helper.Constants;
import kz.topsecurity.top_signal.service.trackingService.interfaces.AddressListener;

public class AddressResultReceiver extends ResultReceiver {
    AddressListener mListener;

    public AddressResultReceiver(Handler handler, AddressListener listener) {
        super(handler);
        mListener = listener;
    }

    @Override
    protected void onReceiveResult(int resultCode, final Bundle resultData) {
        Address address = null;
        if (resultCode == Constants.SUCCESS_RESULT) {
            address = resultData.getParcelable(Constants.RESULT_ADDRESS);
        }
        mListener.onAddressChanged(address);
    }
}