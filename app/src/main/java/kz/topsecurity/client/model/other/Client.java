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
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME= "last_name";
    public static final String COLUMN_PATRONYMIC = "patronymic";
    public static final String COLUMN_IIN = "iin";
    public static final String COLUMN_HEALTH_CARD_ID = "health_card_id";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USER_ID + " INTEGER,"
                    + COLUMN_USERNAME+ " TEXT,"
                    + COLUMN_PHONE + " TEXT, "
                    + COLUMN_EMAIL + " TEXT, "
                    + COLUMN_PHOTO + " TEXT, "
                    + COLUMN_FIRST_NAME + " TEXT, "
                    + COLUMN_LAST_NAME + " TEXT, "
                    + COLUMN_PATRONYMIC + " TEXT, "
                    + COLUMN_IIN + " TEXT, "
                    + COLUMN_HEALTH_CARD_ID + " INTEGER "
                    + ")";

    public static final String DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("iin")
    @Expose
    private String iin;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("patronymic")
    @Expose
    private String patronymic;
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
    @SerializedName("healthcard")
    @Expose
    private Healthcard healthcard;

    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public Healthcard getHealthcard() {
        return healthcard;
    }

    public void setHealthcard(Healthcard healthcard) {
        this.healthcard = healthcard;
    }

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
