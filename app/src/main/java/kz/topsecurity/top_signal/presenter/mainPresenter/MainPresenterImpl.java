package kz.topsecurity.top_signal.presenter.mainPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.top_signal.model.alert.CheckAlertResponse;
import kz.topsecurity.top_signal.presenter.base.BasePresenterImpl;
import kz.topsecurity.top_signal.service.api.RequestService;
import kz.topsecurity.top_signal.service.api.RetrofitClient;
import kz.topsecurity.top_signal.view.mainView.MainView;

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
            view.onAlertNotActive();
        }
    }

    @Override
    public void detach() {
        super.detach();
    }
}
