package kz.topsecurity.client.service.trackingService.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CellTower {

    public CellTower(Integer cellId, Integer locationAreaCode, Integer mobileCountryCode, Integer mobileNetworkCode) {
        this.cellId = cellId;
        this.locationAreaCode = locationAreaCode;
        this.mobileCountryCode = mobileCountryCode;
        this.mobileNetworkCode = mobileNetworkCode;
    }

    @SerializedName("cellId")
    @Expose
    private Integer cellId;
    @SerializedName("locationAreaCode")
    @Expose
    private Integer locationAreaCode;
    @SerializedName("mobileCountryCode")
    @Expose
    private Integer mobileCountryCode;
    @SerializedName("mobileNetworkCode")
    @Expose
    private Integer mobileNetworkCode;

    public Integer getCellId() {
        return cellId;
    }

    public void setCellId(Integer cellId) {
        this.cellId = cellId;
    }

    public Integer getLocationAreaCode() {
        return locationAreaCode;
    }

    public void setLocationAreaCode(Integer locationAreaCode) {
        this.locationAreaCode = locationAreaCode;
    }

    public Integer getMobileCountryCode() {
        return mobileCountryCode;
    }

    public void setMobileCountryCode(Integer mobileCountryCode) {
        this.mobileCountryCode = mobileCountryCode;
    }

    public Integer getMobileNetworkCode() {
        return mobileNetworkCode;
    }

    public void setMobileNetworkCode(Integer mobileNetworkCode) {
        this.mobileNetworkCode = mobileNetworkCode;
    }

}
