package kz.topsecurity.client.model.alertList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import kz.topsecurity.client.model.other.BasicResponseTemplate;

public class AlertsListResponse extends BasicResponseTemplate {
    @SerializedName("alerts")
    @Expose
    private List<Alert> alerts = null;

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }
}
