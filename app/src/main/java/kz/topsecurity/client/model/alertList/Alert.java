package kz.topsecurity.client.model.alertList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import kz.topsecurity.client.model.contact.Contact;
import kz.topsecurity.client.model.other.Client;

public class Alert {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("client_id")
    @Expose
    private Integer clientId;
    @SerializedName("device_id")
    @Expose
    private Integer deviceId;
    @SerializedName("tracking_device_data_id")
    @Expose
    private Integer trackingDeviceDataId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("client")
    @Expose
    private Client client;
    @SerializedName("contacts")
    @Expose
    private List<Contact> contacts = null;
    @SerializedName("logs")
    @Expose
    private List<Log> logs = null;
    @SerializedName("tracking")
    @Expose
    private Tracking tracking;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getTrackingDeviceDataId() {
        return trackingDeviceDataId;
    }

    public void setTrackingDeviceDataId(Integer trackingDeviceDataId) {
        this.trackingDeviceDataId = trackingDeviceDataId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public Tracking getTracking() {
        return tracking;
    }

    public void setTracking(Tracking tracking) {
        this.tracking = tracking;
    }

}
