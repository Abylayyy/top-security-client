package kz.topsecurity.client.model.place;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import kz.topsecurity.client.model.other.BasicResponseTemplate;

public class GetPlaceResponse extends BasicResponseTemplate {

    @SerializedName("places")
    @Expose
    private List<Place> places = null;

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }
}
