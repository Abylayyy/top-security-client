package kz.topsecurity.client.service.trackingService.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetCellTowerLocationResponse {
    @SerializedName("location")
    @Expose
    private TowerLocation location;
    @SerializedName("accuracy")
    @Expose
    private Integer accuracy;

    public TowerLocation getLocation() {
        return location;
    }

    public void setLocation(TowerLocation location) {
        this.location = location;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }

}
