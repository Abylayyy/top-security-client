package kz.topsecurity.client.presenter.placesPresenter;

import com.google.android.gms.maps.model.LatLng;

import kz.topsecurity.client.presenter.base.BasePresenter;
import kz.topsecurity.client.view.base.BaseView;

public interface PlacesPresenter extends BasePresenter {
    void getPlaces();
    void savePlace(String s, LatLng markerLocation,String description, int mRadius);
    void deletePlace(Integer id);

    void editPlace(int edit_place_id, String s, LatLng markerLocation,String description, int mRadius);
}
