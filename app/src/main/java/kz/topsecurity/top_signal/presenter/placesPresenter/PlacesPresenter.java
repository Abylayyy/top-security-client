package kz.topsecurity.top_signal.presenter.placesPresenter;

import com.google.android.gms.maps.model.LatLng;

import kz.topsecurity.top_signal.presenter.base.BasePresenter;
import kz.topsecurity.top_signal.view.base.BaseView;

public interface PlacesPresenter extends BasePresenter {
    void getPlaces();
    void savePlace(String s, LatLng markerLocation, int mRadius);
    void deletePlace(Integer id);
}
