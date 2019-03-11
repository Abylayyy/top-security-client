package kz.topsecurity.client.model.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import kz.topsecurity.client.model.other.BasicResponseTemplate;
import kz.topsecurity.client.model.other.Client;

public class GetClientResponse  extends BasicResponseTemplate {

    @SerializedName("client")
    @Expose
    private Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
