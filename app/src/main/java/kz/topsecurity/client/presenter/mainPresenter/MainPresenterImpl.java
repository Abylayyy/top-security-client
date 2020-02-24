package kz.topsecurity.client.presenter.mainPresenter;

import android.content.Context;
import android.os.Handler;
import com.google.firebase.iid.FirebaseInstanceId;
import java.lang.ref.WeakReference;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.client.application.TopSecurityClientApplication;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.alert.AlertStatusResponse;
import kz.topsecurity.client.model.alert.CheckAlertResponse;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.presenter.base.BasePresenterImpl;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.view.mainView.MainView;

import static kz.topsecurity.client.domain.MainScreen.AlertActivity.MRRT_CHANGED_POSIITION;
import static kz.topsecurity.client.domain.MainScreen.AlertActivity.ORDER_ACCEPTED;
import static kz.topsecurity.client.domain.MainScreen.AlertActivity.ALERT_SEND;
import static kz.topsecurity.client.domain.MainScreen.AlertActivity.ORDER_CREATED;

public class MainPresenterImpl extends BasePresenterImpl<MainView> implements MainPresenter {

    private boolean isAlertActive = false;
    Handler handler ;
    public MainPresenterImpl(MainView view) {
        super(view);
    }

    public void setAlertActive(boolean alertActive) {
        isAlertActive = alertActive;
    }

    public void removeHandlerCallbacks(){
        handler.removeCallbacks(mRunnable);
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
        view.onAlert(0);
    }
    @Override
    public void checkStatus() {
        handler = new Handler();
        handler.post(mRunnable);
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Disposable subscribe = new RequestService<>(new RequestService.RequestResponse<CheckAlertResponse>() {
                @Override
                public void onSuccess(CheckAlertResponse r) {
                    if(r.getAlert()!=null && !r.getAlert().getStatus().equals("cancelled"))
                        processCheckStatus(true);
                    else {
                        processCheckStatus(false);
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

            handler.postDelayed(mRunnable, Constants.TIMER_WAKE_UP_INTERVAL);
        }
    };

    void processCheckStatus(boolean isSuccessfull){
        if(isSuccessfull){
            isAlertActive = true;
            view.onAlertIsActive();
            checkExactStatus();
        }
        else {
            if (!SharedPreferencesManager.getIsAlertOnHold(TopSecurityClientApplication.getInstance())) {
                isAlertActive = false;
                Constants.is_service_sending_alert(false);
                view.onAlertNotActive();
            } else {
                isAlertActive = true;
                view.onAlertIsActive();
            }
        }
    }

    private void checkExactStatus() {
        Disposable subscribe = new RequestService<>(new RequestService.RequestResponse<AlertStatusResponse>() {
            @Override
            public void onSuccess(AlertStatusResponse r) {
                processAlertExactStatus(r.getAlertStatus());
            }
            @Override
            public void onFailed(AlertStatusResponse data, int error_message) { }
            @Override
            public void onError(Throwable e, int error_message) { }
        }).makeRequest(RetrofitClient
                .getClientApi()
                .getAlertStatus(RetrofitClient.getRequestToken()));
        compositeDisposable.add(subscribe);
    }

    private void processAlertExactStatus(String alertStatus) {
        switch (alertStatus){
            case Constants.ALERT_STATUS.ENEW:{
                view.setAnimationStatus(ORDER_CREATED);
                break;
            }
            case Constants.ALERT_STATUS.ACCEPTED:{
                view.setAnimationStatus(ORDER_ACCEPTED);
                break;
            }
            case Constants.ALERT_STATUS.CANCELLED:{
                break;
            }
            case Constants.ALERT_STATUS.IN_PROCESS:{
                view.setAnimationStatus(MRRT_CHANGED_POSIITION);
                break;
            }
            case Constants.ALERT_STATUS.FINISHED:{
                view.setAnimationStatus(ALERT_SEND);
                break;
            }

        }
    }

    @Override
    public void detach() {
        super.detach();
    }

    @Override
    public void logToken(){
        String token = FirebaseInstanceId.getInstance().getToken();
        WeakReference data = new WeakReference<>(token);
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
        data.clear();
    }



    @Override
    public void updateDrawerData(Context context) {
        Client clientData = (new DataBaseManagerImpl(context)).getClientData();
        WeakReference data = new WeakReference<>(clientData);
        if(clientData!=null){
            view.setDrawerData(clientData);
       }
        data.clear();
    }

    @Override
    public void exitFromApplication(Context context) {
        Constants.clearData(context);
        view.exitFromMainScreen();
    }

    @Override
    public void checkPlan() {
        Disposable success = RetrofitClient.getClientApi().getClientData(RetrofitClient.getRequestToken())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> {
                            if(r.getClient().getPlan()!=null && !r.getClient().getPlan().getIsExpired()){
                                SharedPreferencesManager.setUserPaymentIsActive(TopSecurityClientApplication.getInstance(),true);
                                view.userPlanChanged(true);
                            }
                        },
                        e -> {
                        });
        compositeDisposable.add(success);
    }

    @Override
    public void callAmbulance() {
        if(isAlertActive)
            return;

        view.onAlert(2);
    }

    @Override
    public void callMeBack() {
        if(isAlertActive)
            return;

        view.onAlert(1);
    }
}
