package kz.topsecurity.client.model.other;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public abstract class BasicErrorTemplate {

    @SerializedName("exception")
    @Expose
    private String exception = null;

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
