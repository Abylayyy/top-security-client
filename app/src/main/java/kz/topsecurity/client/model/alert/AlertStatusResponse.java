package kz.topsecurity.client.model.alert;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import kz.topsecurity.client.model.other.BasicResponseTemplate;

public class AlertStatusResponse extends BasicResponseTemplate {
    @SerializedName("alert_status")
    @Expose
    private String alertStatus;

    public String getAlertStatus() {
        return alertStatus;
    }

    public void setAlertStatus(String alertStatus) {
        this.alertStatus = alertStatus;
    }

}