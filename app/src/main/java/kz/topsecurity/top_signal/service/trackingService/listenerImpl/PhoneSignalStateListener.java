package kz.topsecurity.top_signal.service.trackingService.listenerImpl;

import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;


import kz.topsecurity.top_signal.service.trackingService.interfaces.SignalListener;
import kz.topsecurity.top_signal.service.trackingService.model.CellTower;

public class PhoneSignalStateListener extends PhoneStateListener {
    int mSignalStrength = 0;
    SignalListener listener;
    private static final String TAG = PhoneSignalStateListener.class.getSimpleName();
    boolean isTowerCellLocationRetrived = false;

    int mMcc = 0;
    int mMnc = 0;
    int mCellid = 0;
    int mCelllac = 0;

    public PhoneSignalStateListener(SignalListener listener,int mcc , int mnc){
        this.listener = listener;
        this.mMcc = mcc;
        this.mMnc = mnc;
    }

    @Override
    public void onCellLocationChanged(CellLocation location) {
        super.onCellLocationChanged(location);
        int cid = 0;
        int lac = 0;
        boolean is_gsm_data_retrived = false;
        if (location != null) {
            if (location instanceof GsmCellLocation) {
                cid = ((GsmCellLocation) location).getCid();
                lac = ((GsmCellLocation) location).getLac();
            }
            else if (location instanceof CdmaCellLocation) {
                cid = ((CdmaCellLocation) location).getBaseStationId();
                lac = ((CdmaCellLocation) location).getSystemId();
            }
            is_gsm_data_retrived = true;
        }

        Log.d("GSM CELL ID",  String.valueOf(cid));
        Log.d("GSM Location Code", String.valueOf(lac));

        if(is_gsm_data_retrived && isGsmDataChanged(cid,lac)){
            listener.onCellTowerDataChanged(new CellTower(cid,lac,mMcc,mMnc));
        }
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        mSignalStrength = signalStrength.getGsmSignalStrength();
        mSignalStrength = (2 * mSignalStrength) - 113; // -> dBm
        listener.onSignalChanged(mSignalStrength);
    }

    private boolean isGsmDataChanged(int cellid , int celllac ){ return cellid != mCellid && celllac != mCelllac; }

    public PhoneSignalStateListener(SignalListener listener){
        this.listener = listener;
    }
}