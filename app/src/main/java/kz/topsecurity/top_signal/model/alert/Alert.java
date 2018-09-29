package kz.topsecurity.top_signal.model.alert;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Alert {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("client_id")
    @Expose
    private Integer clientId;
    @SerializedName("device_id")
    @Expose
    private Integer deviceId;
    @SerializedName("tracking_device_data_id")
    @Expose
    private Integer trackingDeviceDataId;
    @SerializedName("status")
    @Expose
    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getTrackingDeviceDataId() {
        return trackingDeviceDataId;
    }

    public void setTrackingDeviceDataId(Integer trackingDeviceDataId) {
        this.trackingDeviceDataId = trackingDeviceDataId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}