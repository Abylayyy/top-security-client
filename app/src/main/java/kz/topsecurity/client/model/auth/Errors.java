package kz.topsecurity.client.model.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Errors {
    @SerializedName("phone")
    @Expose
    private List<String> phone = null;
    @SerializedName("password")
    @Expose
    private List<String> password = null;
    @SerializedName("udid")
    @Expose
    private List<String> udid = null;
    @SerializedName("type")
    @Expose
    private List<String> type = null;

    public List<String> getPhone() {
        return phone;
    }

    public void setPhone(List<String> phone) {
        this.phone = phone;
    }

    public List<String> getPassword() {
        return password;
    }

    public void setPassword(List<String> password) {
        this.password = password;
    }

    public List<String> getUdid() {
        return udid;
    }

    public void setUdid(List<String> udid) {
        this.udid = udid;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

}
