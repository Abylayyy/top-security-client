package kz.topsecurity.client.service.trackingService.managers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import kz.topsecurity.client.R;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.service.trackingService.model.WeatherData;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static android.content.Context.SENSOR_SERVICE;

public class BarometricAltitudeListenerManager implements SensorEventListener {

    private static final String TAG = BarometricAltitudeListenerManager.class.getSimpleName();
    private SensorManager mSensorManager;
    private float mPressureAltitudeValue=0;
    private boolean blockSensorAccordingToAccuracy = false;
    private double AtmosphericPressure = SensorManager.PRESSURE_STANDARD_ATMOSPHERE;
    private okhttp3.Call weatherCall;
    private String mUrl;
    private boolean active;
    private long lastRequestTime = -1;

    public float getPressureAltitudeValue() {
        return mPressureAltitudeValue;
    }

    public void setupBarometricAltitude(Context context){
        //TODO: coordinates
        mUrl = String.format("%s?q=%s&appId=%s", Constants.WheatherApiBaseURL,"Almaty,kz",context.getString(R.string.weather_api_key));
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        if (mSensorManager!=null && isSensorAvailable(context)) {
            Sensor mBarometer = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            mSensorManager.registerListener(this, mBarometer, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "ALTITUDE TRACKER CONNECTING");
            active = true;
        }
        else{
            Log.d(TAG, "ALTITUDE TRACKER IS UNAVAILABLE. CAUSE : HARDWARE");
        }
    }

    boolean isSensorAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE){
            float pressureValue = sensorEvent.values[0];
            float v = (float) AtmosphericPressure ;
            mPressureAltitudeValue = SensorManager.getAltitude(v,pressureValue);
        }
        else{
            Log.d(TAG," SENSOR Triggered");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        if(sensor.getType() == Sensor.TYPE_PRESSURE) {
            blockSensorAccordingToAccuracy = true;
            if(i == SensorManager.SENSOR_STATUS_ACCURACY_LOW){
                blockSensorAccordingToAccuracy = true;
            }
        }
    }

    public void getWeatherData() throws IOException {
        long current_request = System.currentTimeMillis();
        if(current_request- lastRequestTime <15*60*1000){
            return;
        }
        lastRequestTime = current_request;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(mUrl)
                .build();
        weatherCall = client.newCall(request);
        weatherCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if(response.code()==200) {
                    String json_response = response.body().string();
                    WeatherData.WeatherDataResponse weather_data = new Gson().fromJson(json_response, WeatherData.WeatherDataResponse.class);
                    AtmosphericPressure = weather_data.getMain().getPressure();
                }
            }
        });
    }

    public void stop(){
        active = false;
        if(mSensorManager!=null)
            mSensorManager.unregisterListener(this);
        if(weatherCall!=null)
            weatherCall.cancel();
    }

    public boolean isActive() {
        return active;
    }
}
