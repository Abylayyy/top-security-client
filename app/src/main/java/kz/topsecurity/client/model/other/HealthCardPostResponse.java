package kz.topsecurity.client.model.other;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HealthCardPostResponse extends BasicResponse {
    public Healthcard getHealthcard() {
        return healthcard;
    }

    public void setHealthcard(Healthcard healthcard) {
        this.healthcard = healthcard;
    }

    @SerializedName("healthcard")
    @Expose
    private Healthcard healthcard;

}

