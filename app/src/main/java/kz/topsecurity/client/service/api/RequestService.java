package kz.topsecurity.client.service.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.client.application.TopSecurityClientApplication;
import kz.topsecurity.client.helper.TextHelper;
import kz.topsecurity.client.model.other.BasicResponseTemplate;

public class RequestService<T> {

    public interface RequestResponse<T>{
        void onSuccess(T data);
        void onFailed(T data, int error_message);
        void onError(Throwable e, int error_message);
    }

    private RequestResponse<T> listener ;

    public RequestService(RequestResponse<T> listener){
        this.listener = listener;
    }

    public Disposable makeRequest(Observable<T> request){
        return request.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r->{
                            BasicResponseTemplate result = (BasicResponseTemplate)r;
                            if(!result.getStatus().equals("success")){
                                listener.onFailed(r , processError(result.getStatusCode()));
                            }
                            else{
                                listener.onSuccess(r);
                            }
                        },
                e->{
                    if(isNetworkOnline())
                        listener.onError(e, processError(-1));
                    else
                        listener.onError(e, processError(-2));
                });
    }

    private int processError(int errorCode){
        return TextHelper.getErrorMessage(errorCode);
    }

    public boolean isNetworkOnline() {
        boolean status=false;
        WeakReference data = null;
        try{
            ConnectivityManager cm = (ConnectivityManager) TopSecurityClientApplication.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            data = new WeakReference<>(cm);
            if(cm!=null) {
                NetworkInfo netInfo = cm.getNetworkInfo(0);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    status = true;
                } else {
                    netInfo = cm.getNetworkInfo(1);
                    if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                        status = true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            if(data!=null)
                data.clear();
        }
        return status;
    }
}
