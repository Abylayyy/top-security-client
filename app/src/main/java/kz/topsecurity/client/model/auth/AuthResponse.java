package kz.topsecurity.client.model.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import kz.topsecurity.client.model.other.BasicResponseTemplate;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.model.other.Token;

public class AuthResponse extends BasicResponseTemplate {

    @SerializedName("client")
    @Expose
    private Client client;
    @SerializedName("token")
    @Expose
    private Token token;
    @SerializedName("errors")
    @Expose
    private Errors errors;

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
