package kz.topsecurity.client.service.trackingService.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceData {
    public static final String TABLE_NAME = "device_data";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATA_ID = "data_id";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";
    public static final String COLUMN_ALT = "alt";
    public static final String COLUMN_ALT_BAROMETER = "alt_barometer";
    public static final String COLUMN_STREET_ADDRESS = "street_address";
    public static final String COLUMN_SIGNAL_STRENGTH = "signal_strength";
    public static final String COLUMN_CHARGE = "charge";
    public static final String COLUMN_IS_URGENT = "is_urgent";
    public static final String COLUMN_IS_GPS_ACTIVE = "is_gps_active";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_CREATED_AT = "created_at";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_DATA_ID+ " INTEGER,"
                    + COLUMN_LAT + " TEXT, "
                    + COLUMN_LNG + " TEXT, "
                    + COLUMN_ALT + " TEXT, "
                    + COLUMN_ALT_BAROMETER + " TEXT, "
                    + COLUMN_STREET_ADDRESS + " TEXT, "
                    + COLUMN_SIGNAL_STRENGTH + " INTEGER, "
                    + COLUMN_CHARGE + " INTEGER, "
                    + COLUMN_IS_URGENT + " INTEGER, "
                    + COLUMN_IS_GPS_ACTIVE + " INTEGER, "
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP "
                   // + COLUMN_CREATED_AT + " TEXT "
                    + ")";

    public static final String DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DeviceData(){
        setIs_urgent(false);
        setIs_gps_active(false);
    }

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("device_id")
    @Expose
    private Integer deviceId;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;
    @SerializedName("alt")
    @Expose
    private Double alt;
    @SerializedName("alt_barometer")
    @Expose
    private Double altBarometer;
    @SerializedName("street_address")
    @Expose
    private String streetAddress;
    @SerializedName("signal_strength")
    @Expose
    private Integer signalStrength;
    @SerializedName("charge")
    @Expose
    private Integer charge;
    @SerializedName("is_urgent")
    @Expose
    private Boolean is_urgent;
    @SerializedName("is_gps_active")
    @Expose
    private Boolean is_gps_active;
    @SerializedName("timestamp")
    @Expose
    private Integer timestamp;
    @SerializedName("created_at")
    @Expose
    private CreatedAt createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getAlt() {
        return alt;
    }

    public void setAlt(Double alt) {
        this.alt = alt;
    }

    public Double getAltBarometer() {
        return altBarometer;
    }

    public void setAltBarometer(Double altBarometer) {
        this.altBarometer = altBarometer;
    }

    public Object getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(Integer signalStrength) {
        this.signalStrength = signalStrength;
    }

    public Integer getCharge() {
        return charge;
    }

    public void setCharge(Integer charge) {
        this.charge = charge;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public CreatedAt getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(CreatedAt createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIs_urgent() {
        return is_urgent;
    }

    public void setIs_urgent(Boolean is_urgent) {
        this.is_urgent = is_urgent;
    }

    public Boolean getIs_gps_active() {
        return is_gps_active;
    }

    public void setIs_gps_active(Boolean is_gps_active) {
        this.is_gps_active = is_gps_active;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }
}