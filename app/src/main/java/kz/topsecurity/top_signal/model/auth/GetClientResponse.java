package kz.topsecurity.top_signal.model.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import kz.topsecurity.top_signal.model.other.BasicResponseTemplate;
import kz.topsecurity.top_signal.model.other.Client;

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
