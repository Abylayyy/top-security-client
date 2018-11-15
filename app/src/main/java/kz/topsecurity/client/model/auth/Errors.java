package kz.topsecurity.client.model.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import kz.topsecurity.client.model.other.BasicErrorTemplate;

public class Errors extends BasicErrorTemplate {

    @SerializedName("type")
    @Expose
    private String type = null;



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
