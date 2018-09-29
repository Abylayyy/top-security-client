package kz.topsecurity.top_signal.service.api;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.top_signal.helper.TextHelper;
import kz.topsecurity.top_signal.model.other.BasicResponseTemplate;

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
                    listener.onError(e, processError(-1));
                });
    }

    private int processError(int errorCode){
        return TextHelper.getErrorMessage(errorCode);
    }
}
