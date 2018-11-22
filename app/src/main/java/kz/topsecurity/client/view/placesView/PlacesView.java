package kz.topsecurity.client.view.placesView;

import com.google.android.gms.location.places.Places;

import java.util.List;

import kz.topsecurity.client.model.place.Place;
import kz.topsecurity.client.view.base.BaseView;

public interface PlacesView extends BaseView {
    void onPlacesLoaded(List<Place> places);
    void onPlacesLoadError(int error_message);
    void hideLoadingDialog();
    void showLoadingDialog();
    void onPlaceSaveError(int error);
    void onPlaceSaved(Place place);
    void onPlaceDeleteError(int error);
    void onPlaceDeleteSuccess(int id);

    void onPlaceEditSuccess(Place place,int edit_place_id);

    void onPlaceEditError(int error);
}
