package kz.topsecurity.client.presenter.placesPresenter;

import com.google.android.gms.maps.model.LatLng;

import kz.topsecurity.client.presenter.base.BasePresenter;
import kz.topsecurity.client.view.base.BaseView;

public interface PlacesPresenter extends BasePresenter {
    void getPlaces();
    void savePlace(String s, LatLng markerLocation, int mRadius);
    void deletePlace(Integer id);
}
