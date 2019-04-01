package kz.topsecurity.client.presenter.placesPresenter;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.client.R;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.model.auth.AuthResponse;
import kz.topsecurity.client.model.other.BasicResponse;
import kz.topsecurity.client.model.place.GetPlaceResponse;
import kz.topsecurity.client.model.place.Place;
import kz.topsecurity.client.model.place.SavePlaceResponse;
import kz.topsecurity.client.presenter.base.BasePresenterImpl;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.view.placesView.PlacesView;

public class PlacesPresenterImpl extends BasePresenterImpl<PlacesView> implements PlacesPresenter {

    public PlacesPresenterImpl(PlacesView view) {
        super(view);
    }

    @Override
    public void getPlaces() {
        view.showLoadingDialog();

        Disposable subscribe = new RequestService<>(new RequestService.RequestResponse<GetPlaceResponse>() {
            @Override
            public void onSuccess(GetPlaceResponse r) {

                view.hideLoadingDialog();

                if(r.getPlaces()==null)
                    view.onPlacesLoaded(new ArrayList<>());
                else
                {
                    view.onPlacesLoaded(r.getPlaces());
                }
            }

            @Override
            public void onFailed(GetPlaceResponse data, int error_message) {
                view.hideLoadingDialog();
                setGetPlaceError(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                view.hideLoadingDialog();
                setGetPlaceError(error_message);
            }

        }).makeRequest(RetrofitClient.getClientApi()
                .getPlaces(RetrofitClient.getRequestToken()));

        compositeDisposable.add(subscribe);
    }

    void setGetPlaceError(int error_message){
        view.onPlacesLoadError(error_message);
    }


    @Override
    public void savePlace(String s, LatLng markerLocation,String description, int mRadius) {
        String lat = String.valueOf(markerLocation.latitude);
        String lng = String.valueOf(markerLocation.longitude);

        Disposable success = new RequestService<>(new RequestService.RequestResponse<SavePlaceResponse>() {
            @Override
            public void onSuccess(SavePlaceResponse r) {
                if(r.getPlace()==null)
                    setSavePlaceError(R.string.unable_to_save_place);
                else
                {
                    view.hideLoadingDialog();
                    view.onPlaceSaved(r.getPlace());
                }
            }

            @Override
            public void onFailed(SavePlaceResponse data, int error_message) {
                setSavePlaceError(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                setSavePlaceError(error_message);
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .savePlace(RetrofitClient.getRequestToken(), s, lat, lng, description, mRadius));

        compositeDisposable.add(success);
    }

    @Override
    public void deletePlace(Integer id) {
        view.showLoadingDialog();

        Disposable success = new RequestService<>(new RequestService.RequestResponse<BasicResponse>() {
            @Override
            public void onSuccess(BasicResponse r) {
                deletePlaceSuccess(id);
            }

            @Override
            public void onFailed(BasicResponse data, int error_message) {
                deletePlaceError(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                deletePlaceError(error_message);
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .deletePlace(RetrofitClient.getRequestToken(), id));

        compositeDisposable.add(success);
    }

    @Override
    public void editPlace(int edit_place_id, String s, LatLng markerLocation,String description, int mRadius) {
        view.showLoadingDialog();
        String lat = String.valueOf(markerLocation.latitude);
        String lng = String.valueOf(markerLocation.longitude);

        Disposable success = new RequestService<>(new RequestService.RequestResponse<SavePlaceResponse>() {
            @Override
            public void onSuccess(SavePlaceResponse r) {
                editPlaceSuccess(r.getPlace(), edit_place_id);
            }

            @Override
            public void onFailed(SavePlaceResponse data, int error_message) {
                editPlaceError(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                deletePlaceError(error_message);
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .editPlace(edit_place_id, RetrofitClient.getRequestToken(), s, lat, lng,description, mRadius));

        compositeDisposable.add(success);
    }

    private void editPlaceError(int error) {
        view.hideLoadingDialog();
        view.onPlaceEditError(error);
    }

    private void editPlaceSuccess(Place place , int edit_place_id) {
        view.hideLoadingDialog();
        view.onPlaceEditSuccess(place,edit_place_id);
    }

    private void deletePlaceSuccess(int id) {
        view.hideLoadingDialog();
        view.onPlaceDeleteSuccess(id);
    }

    private void deletePlaceError(int error) {
        view.hideLoadingDialog();
        view.onPlaceDeleteError(error);
    }

    void setSavePlaceError(int error){
        view.hideLoadingDialog();
        view.onPlaceSaveError(error);
    }

    public void detach(){
        compositeDisposable.clear();
    }
}
