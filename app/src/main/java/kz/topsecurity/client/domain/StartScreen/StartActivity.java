package kz.topsecurity.client.domain.StartScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.MainScreen.MainActivity;
import kz.topsecurity.client.domain.PaymentScreen.PaymentActivity;
import kz.topsecurity.client.domain.RegisterScreen.RegisterActivity;
import kz.topsecurity.client.domain.LoginScreen.LoginActivity;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.auth.GetClientResponse;
import kz.topsecurity.client.model.other.BasicResponse;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import retrofit2.Retrofit;

public class StartActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.cl_loading_view) ConstraintLayout cl_loading_view;
    @BindView(R.id.cl_start_view) ConstraintLayout cl_start_view;
    @BindView(R.id.btn_sign_in) Button btn_sign_in;
    @BindView(R.id.btn_sign_up) Button btn_sign_up;

    public static final String START_MAIN_SCREEN_KEY = "START_MAIN_SCREEN";
    public static final String SKIP_LOADING_KEY = "SKIP_LOADING";

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        btn_sign_in.setOnClickListener(this);
        btn_sign_up.setOnClickListener(this);

        showLoadingView();

        boolean startMainScreen = getIntent().getBooleanExtra(START_MAIN_SCREEN_KEY , false) ;
        boolean skipLoading= getIntent().getBooleanExtra(SKIP_LOADING_KEY, false);
        boolean isUserLoggedIn = SharedPreferencesManager.getUserData(this) && SharedPreferencesManager.getUserAuthToken(this)!=null;
        String userPhone = SharedPreferencesManager.getUserPhone(this);
        if(skipLoading){
            showStartView();
        }
        else if (startMainScreen ){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
        else if(isUserLoggedIn){
            startWithLogin();
        }
        else{
            mimicLoading();
        }
    }

    private void startWithLogin() {
        Disposable success = new RequestService<>(new RequestService.RequestResponse<GetClientResponse>() {
            @Override
            public void onSuccess(GetClientResponse r) {
               if( r.getClient() == null)
                   onLoginFailed();

               else
                   onSuccessLogin(r.getClient());
            }

            @Override
            public void onFailed(GetClientResponse data, int error_message) {
                onLoginFailed();
            }

            @Override
            public void onError(Throwable e, int error_message) {
                onLoginFailed();
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .getClientData(RetrofitClient.getRequestToken()));

        compositeDisposable.add(success);
    }

    private void onLoginFailed() {
        showStartView();
    }

    DataBaseManager dataBaseManager = new DataBaseManagerImpl(this);

    private void onSuccessLogin(Client client) {
        dataBaseManager.saveClientData(client);
        if(Constants.IS_DEBUG || (client.getPlan()!=null && !client.getPlan().getIsExpired())) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else{
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra(PaymentActivity.FORCED,true);
            startActivity(intent);
            finish();
        }
    }

    void mimicLoading(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showStartView();
                    }
                });
            }
        },2000);

    }

    void showLoadingView(){
        cl_loading_view.setVisibility(View.VISIBLE);
        cl_start_view.setVisibility(View.GONE);
    }

    void showStartView(){
        cl_loading_view.setVisibility(View.GONE);
        cl_start_view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_sign_in:{
                login();
                break;
            }
            case R.id.btn_sign_up:{
                register();
                break;
            }
            default:{

                break;
            }
        }
    }

    private void login() {
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

    private void register() {
        startActivity(new Intent(this,RegisterActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
