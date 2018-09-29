package kz.topsecurity.top_signal.service.trackingService.managers;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import kz.topsecurity.top_signal.R;
import kz.topsecurity.top_signal.helper.Constants;
import kz.topsecurity.top_signal.service.trackingService.interfaces.SignalListener;
import kz.topsecurity.top_signal.service.trackingService.listenerImpl.PhoneSignalStateListener;
import kz.topsecurity.top_signal.service.trackingService.model.CellTower;
import kz.topsecurity.top_signal.service.trackingService.model.GetCellTowerLocationRequest;
import kz.topsecurity.top_signal.service.trackingService.model.GetCellTowerLocationResponse;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import static com.example.sample.trackingapp.services.trakingServices.NewTrackingService.JSON;

public class TelephonyListenerManager implements SignalListener {

    private static final String TAG = TelephonyListenerManager.class.getSimpleName();
    private String mUrl;

    TelephonyManager mTelephonyManager;
    PhoneSignalStateListener mPhoneStateListener;
    int mSignalStrength = 0;
    private Double mLatitude = 0.0;
    private Double mLongitude = 0.0;
    private boolean mIsActive = false;

    public Double getLatitude() {
        return mLatitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public TelephonyListenerManager(){}

    public void setupTelephonyListener(Context context){
        mUrl = String.format("%s?key=%s", Constants.GoogleApiBaseURL,context.getString(R.string.google_server_maps_key));
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = mTelephonyManager.getNetworkOperator();
        int mcc =0;
        int mnc =0;
        if (!TextUtils.isEmpty(networkOperator)) {
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mnc = Integer.parseInt(networkOperator.substring(3));
        }
        if(mcc!=0 && mnc!=0) {
            mPhoneStateListener = new PhoneSignalStateListener(this, mcc,mnc);
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CELL_LOCATION);
            Log.d(TAG, "SIGNAL TRACKER CONNECTING");
        }
        mIsActive = true;
    }

    @Override
    public void onSignalChanged(int signalStrength) {
        Log.d(TAG, String.format("SIGNAL STRENGTH : %d",signalStrength));
        mSignalStrength = signalStrength;
    }

    @Override
    public void onCellTowerDataChanged(CellTower data) {
        //IS GPS ENABLED
        GetCellTowerLocationRequest getCellTowerLocationRequest = new GetCellTowerLocationRequest();
        getCellTowerLocationRequest.setCellTowers(new ArrayList<CellTower>());
        getCellTowerLocationRequest.getCellTowers().add(data);
        String jsonData =  new Gson().toJson(getCellTowerLocationRequest);
        String jsonResponse = "";
        try {
            getCellTowerLocation(mUrl, jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Call towerLocationCall;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private void getCellTowerLocation(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        towerLocationCall = client.newCall(request);
        towerLocationCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200) {
                    TelephonyListenerManager.this.onResponse(response);
                }
            }
        });
    }

    void onResponse(Response response) throws IOException {
        String json_response = response.body().string();
        GetCellTowerLocationResponse tower_data = new Gson().fromJson(json_response, GetCellTowerLocationResponse.class);
        mLatitude = tower_data.getLocation().getLat();
        mLongitude = tower_data.getLocation().getLng();
    }

    public void stop(){
        if(towerLocationCall!=null)
            towerLocationCall.cancel();
        if(mTelephonyManager!=null && mPhoneStateListener!=null)
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        mIsActive = false;
    }

    public boolean isActive() {
        return mIsActive;
    }
}
