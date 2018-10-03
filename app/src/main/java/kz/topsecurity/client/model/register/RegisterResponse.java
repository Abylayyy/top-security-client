package kz.topsecurity.client.model.register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import kz.topsecurity.client.model.other.BasicResponseTemplate;
import kz.topsecurity.client.model.other.Client;

public class RegisterResponse extends BasicResponseTemplate{

    @SerializedName("errors")
    @Expose
    private Errors errors;

    @SerializedName("client")
    @Expose
    private Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }


}
