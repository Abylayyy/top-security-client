package kz.topsecurity.client.model.other;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import kz.topsecurity.client.model.other.Device;

public class Client {

    public static final String TABLE_NAME = "client_data";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHOTO = "photo";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USER_ID + " INTEGER,"
                    + COLUMN_USERNAME+ " TEXT,"
                    + COLUMN_PHONE + " TEXT, "
                    + COLUMN_EMAIL + " TEXT, "
                    + COLUMN_PHOTO + " TEXT "
                    + ")";

    public static final String DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("email")
    @Expose
    private String  email;
    @SerializedName("devices")
    @Expose
    private List<Device> devices = null;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("plan")
    @Expose
    private Plan plan;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }
}
