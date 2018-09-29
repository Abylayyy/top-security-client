package kz.topsecurity.top_signal.model.other;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Device {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("udid")
    @Expose
    private String udid;
    @SerializedName("phone_number")
    @Expose
    private Object phoneNumber;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("name")
    @Expose
    private Object name;
    @SerializedName("description")
    @Expose
    private Object description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public Object getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Object phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

}