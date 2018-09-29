package kz.topsecurity.top_signal.model.register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Errors {
    @SerializedName("username")
    @Expose
    private List<String> username = null;
    @SerializedName("phone")
    @Expose
    private List<String> phone = null;
    @SerializedName("password")
    @Expose
    private List<String> password = null;

    public List<String> getUsername() {
        return username;
    }

    public void setUsername(List<String> username) {
        this.username = username;
    }

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
}
