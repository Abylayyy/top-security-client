package kz.topsecurity.top_signal.model.alert;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import kz.topsecurity.top_signal.model.other.BasicResponseTemplate;

public class CancelAlertResponse extends BasicResponseTemplate {

    @SerializedName("alert")
    @Expose
    private Alert alert;

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

}
