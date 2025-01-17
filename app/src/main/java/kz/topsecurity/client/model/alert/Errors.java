package kz.topsecurity.client.model.alert;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import kz.topsecurity.client.model.other.BasicErrorTemplate;

public class Errors extends BasicErrorTemplate {
    @SerializedName("lat")
    @Expose
    private List<String> lat = null;
    @SerializedName("lng")
    @Expose
    private List<String> lng = null;
    @SerializedName("timestamp")
    @Expose
    private List<String> timestamp = null;

    public List<String> getLat() {
        return lat;
    }

    public void setLat(List<String> lat) {
        this.lat = lat;
    }

    public List<String> getLng() {
        return lng;
    }

    public void setLng(List<String> lng) {
        this.lng = lng;
    }

    public List<String> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(List<String> timestamp) {
        this.timestamp = timestamp;
    }
}
