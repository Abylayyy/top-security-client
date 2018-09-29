package kz.topsecurity.top_signal.model.contact;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import kz.topsecurity.top_signal.model.other.BasicResponseTemplate;

public class GetContactsResponse extends BasicResponseTemplate {

    @SerializedName("contacts")
    @Expose
    private List<Contact> contacts = null;

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}
