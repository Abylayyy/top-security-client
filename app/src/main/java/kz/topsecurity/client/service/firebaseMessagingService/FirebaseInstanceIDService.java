package kz.topsecurity.client.service.firebaseMessagingService;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.client.application.TopSecurityClientApplication;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.service.api.RetrofitClient;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = FirebaseInstanceIDService.class.getSimpleName();
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        SharedPreferencesManager.setFcmToken(TopSecurityClientApplication.getInstance().getApplicationContext(), refreshedToken);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        String raw_token = RetrofitClient.getRawToken();
        if(raw_token==null || raw_token.isEmpty())
            return;
        String authToken = "Bearer " + raw_token;
        Disposable subscribe = RetrofitClient.getClientApi()
                .setFcmToken(authToken, token)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> {

                }, e -> {

                });

        compositeDisposable.add(subscribe);
    }
}
