package kz.topsecurity.top_signal.model.device;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import kz.topsecurity.top_signal.model.other.BasicResponseTemplate;

public class SaveDeviceDataResponse  extends BasicResponseTemplate {

    @SerializedName("errors")
    @Expose
    private Errors errors;
    @SerializedName("tracking_device_data")
    @Expose
    private TrackingDeviceData trackingDeviceData;

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public TrackingDeviceData getTrackingDeviceData() {
        return trackingDeviceData;
    }

    public void setTrackingDeviceData(TrackingDeviceData trackingDeviceData) {
        this.trackingDeviceData = trackingDeviceData;
    }

}
