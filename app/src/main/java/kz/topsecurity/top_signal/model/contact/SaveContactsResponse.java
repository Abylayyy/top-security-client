package kz.topsecurity.top_signal.model.contact;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import kz.topsecurity.top_signal.model.other.BasicResponseTemplate;

public class SaveContactsResponse  extends BasicResponseTemplate {

    @SerializedName("contact")
    @Expose
    private Contact contact;

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

}
