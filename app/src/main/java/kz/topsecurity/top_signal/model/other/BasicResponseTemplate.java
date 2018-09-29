package kz.topsecurity.top_signal.model.other;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public abstract class BasicResponseTemplate {
    @SerializedName("status")
    @Expose
    protected String status;
    @SerializedName("status_code")
    @Expose
    protected Integer statusCode;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
