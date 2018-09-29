package kz.topsecurity.top_signal.model.place;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import kz.topsecurity.top_signal.model.other.BasicResponseTemplate;

public class SavePlaceResponse extends BasicResponseTemplate{

    @SerializedName("place")
    @Expose
    private Place place;

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

}
