package kz.topsecurity.client.presenter.mainPresenter;

import com.google.firebase.iid.FirebaseInstanceId;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.client.application.TopSecurityClientApplication;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.model.alert.CheckAlertResponse;
import kz.topsecurity.client.presenter.base.BasePresenterImpl;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.view.mainView.MainView;

public class MainPresenterImpl extends BasePresenterImpl<MainView> implements MainPresenter {

    private boolean isAlertActive = false;

    public MainPresenterImpl(MainView view) {
        super(view);
    }

    public void setAlertActive(boolean alertActive) {
        isAlertActive = alertActive;
    }

    @Override
    public void actionWithCheck(){
        if(isAlertActive){
            view.onStoppingAlert();
        }
        else{
            view.onCallingAlert();
        }
    }

    @Override
    public void actionAlert(){
        if(isAlertActive){
            try {
                throw new Exception("UNABLE TO CALL ALERT WHEN ALERT IS ACTIVE");
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                callAlert();
            }
        }
        else{
            view.onCallingAlert();
        }
    }

    @Override
    public void actionCancel(){
        if(isAlertActive){
            view.onStoppingAlert();
        }
        else{
            try {
                throw new Exception("UNABLE TO CANCEL ALERT WHEN ALERT IS NOT ACTIVE");
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                cancelAlert();
            }
        }
    }

    @Override
    public void cancelAlert() {
        isAlertActive = false;
        view.onStopAlert();
    }

    @Override
    public void callAlert(){
        isAlertActive = true;
        view.onAlert();
    }

    @Override
    public void checkStatus() {
        Disposable subscribe = new RequestService<>(new RequestService.RequestResponse<CheckAlertResponse>() {
            @Override
            public void onSuccess(CheckAlertResponse r) {
                if(r.getAlert()!=null && !r.getAlert().getStatus().equals("cancelled"))
                    processCheckStatus(true);
                else {
                    processCheckStatus(false);
//                    Constants.is_service_sending_alert(false);/
//                    view.onCheckAlertServiceIsNotActive();//
                }
            }

            @Override
            public void onFailed(CheckAlertResponse data, int error_message) {
                processCheckStatus(false);
            }

            @Override
            public void onError(Throwable e, int error_message) {
            }
        }).makeRequest(RetrofitClient
                .getClientApi()
                .check(RetrofitClient.getRequestToken()));
        compositeDisposable.add(subscribe);
    }

    void processCheckStatus(boolean isSuccessfull){
        if(isSuccessfull){
            view.onAlertIsActive();
        }
        else{
            if(!SharedPreferencesManager.getIsAlertOnHold(TopSecurityClientApplication.getInstance()))
                view.onAlertNotActive();
            else
                view.onAlertIsActive();
        }
    }

    @Override
    public void detach() {
        super.detach();
    }

    @Override
    public void logToken(){
        String token = FirebaseInstanceId.getInstance().getToken();
        // Log and toast
        if(token == null || token.isEmpty())
            return;

        Disposable subscribe = RetrofitClient.getClientApi()
                .setFcmToken(RetrofitClient.getRequestToken(), token)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> {

                }, e -> {

                });
        compositeDisposable.add(subscribe);
    }
}
