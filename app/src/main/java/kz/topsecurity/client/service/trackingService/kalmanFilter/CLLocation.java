package kz.topsecurity.client.service.trackingService.kalmanFilter;

public class CLLocation {
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private long timestamp;
    private Double accuracy;

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public CLLocation(){

    }

    public CLLocation(Double latitude, Double longitude, Double altitude, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
    }

    public CLLocation(Double latitude, Double longitude, Double altitude, long timestamp, Double accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
        this.accuracy = accuracy;
    }

    public CLLocation(double latitude, double longitude, double altitude, long timestamp, double accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
        this.accuracy = accuracy;
    }
}
