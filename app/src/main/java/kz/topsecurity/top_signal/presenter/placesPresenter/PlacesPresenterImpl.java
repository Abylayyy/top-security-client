package kz.topsecurity.top_signal.presenter.placesPresenter;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.top_signal.R;
import kz.topsecurity.top_signal.helper.Constants;
import kz.topsecurity.top_signal.model.auth.AuthResponse;
import kz.topsecurity.top_signal.model.other.BasicResponse;
import kz.topsecurity.top_signal.model.place.GetPlaceResponse;
import kz.topsecurity.top_signal.model.place.SavePlaceResponse;
import kz.topsecurity.top_signal.presenter.base.BasePresenterImpl;
import kz.topsecurity.top_signal.service.api.RequestService;
import kz.topsecurity.top_signal.service.api.RetrofitClient;
import kz.topsecurity.top_signal.view.placesView.PlacesView;

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
    public void savePlace(String s, LatLng markerLocation, int mRadius) {
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
                .savePlace(RetrofitClient.getRequestToken(), s, lat, lng, mRadius));

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
