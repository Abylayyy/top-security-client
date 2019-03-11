package kz.topsecurity.client.model.alertList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import kz.topsecurity.client.service.trackingService.model.CreatedAt;

public class Tracking {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("device_id")
    @Expose
    private Integer deviceId;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("alt")
    @Expose
    private Double alt;
    @SerializedName("alt_barometer")
    @Expose
    private Double altBarometer;
    @SerializedName("signal_strength")
    @Expose
    private Object signalStrength;
    @SerializedName("charge")
    @Expose
    private Integer charge;
    @SerializedName("is_urgent")
    @Expose
    private Boolean isUrgent;
    @SerializedName("is_gps_active")
    @Expose
    private Boolean isGpsActive;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("created_at")
    @Expose
    private CreatedAt createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getAlt() {
        return alt;
    }

    public void setAlt(Double alt) {
        this.alt = alt;
    }

    public Double getAltBarometer() {
        return altBarometer;
    }

    public void setAltBarometer(Double altBarometer) {
        this.altBarometer = altBarometer;
    }

    public Object getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(Object signalStrength) {
        this.signalStrength = signalStrength;
    }

    public Integer getCharge() {
        return charge;
    }

    public void setCharge(Integer charge) {
        this.charge = charge;
    }

    public Boolean getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(Boolean isUrgent) {
        this.isUrgent = isUrgent;
    }

    public Boolean getIsGpsActive() {
        return isGpsActive;
    }

    public void setIsGpsActive(Boolean isGpsActive) {
        this.isGpsActive = isGpsActive;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public CreatedAt getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(CreatedAt createdAt) {
        this.createdAt = createdAt;
    }
}
