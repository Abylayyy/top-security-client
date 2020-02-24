package kz.topsecurity.client.domain.PaymentScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.MainScreen.AlertActivity;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.service.api.RetrofitClient;

public class PaymentActivity extends BaseActivity {

    public static final String FORCED = "FORCED_PAYMENT";
    private static final int FIRST_CHECK = 1144;
    private static final int LAST_CHECK = 759;
    @BindView(R.id.wv_payment_site)
    WebView wv_payment_site;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean userMadePayment = false;
    String startURL = "";
    @BindView(R.id.profileBottom) LinearLayout podpiska;
    @BindView(R.id.mainBottom) LinearLayout main;
    @BindView(R.id.mainImage2) ImageView mainImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        main.setOnClickListener(v -> {
            Intent intent = new Intent(getApplication(), AlertActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finishAffinity();
        });

        mainImg.setOnClickListener(v -> {
            Intent intent = new Intent(getApplication(), AlertActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finishAffinity();
        });

        checkPlan(FIRST_CHECK);
    }



    private void getPlans() {
        showLoadingDialog();
        Disposable success = RetrofitClient.getClientApi().getPlan(RetrofitClient.getRequestToken())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> {
                            if (!r.getStatus().equals("success")) {
                                hideProgressDialog();
                                finish();
                            } else {
                                if(r.getUrl()!=null){
                                    startURL = r.getUrl();
                                    initWebView(r.getUrl());
                                }
                                hideProgressDialog();
                            }
                        },
                        e -> {
                            hideProgressDialog();
                            finish();
                        });
        compositeDisposable.add(success);
    }

    void initWebView(String url){
        WebSettings webSettings = wv_payment_site.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCacheMaxSize(100 * 1000 * 1000);

        wv_payment_site.setWebChromeClient(new WebChromeClient());
        wv_payment_site.setWebViewClient(new PaymentWebViewClient());
        wv_payment_site.loadUrl(url);
    }

    class PaymentWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            if (Uri.parse(url).getHost().equals("https://www.example.com")) {
//                // This is my website, so do not override; let my WebView load the page
//                return false;
//            }
//
//
            if(url.contains("success") || url.contains("failure"))
            {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(intent);
                userMadePayment = true;
                checkPlan(LAST_CHECK);
               // return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            return false;
        }
    }

    private void checkPlan(int type) {
        showLoadingDialog();
        Disposable success = RetrofitClient.getClientApi().getClientData(RetrofitClient.getRequestToken())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> {
                            boolean status = false;
                            if (!r.getStatus().equals("success")) {
                                hideProgressDialog();
                                if(userMadePayment)
                                    userMadePayment = false;
                            } else {
                                if(r.getClient().getPlan()!=null && !r.getClient().getPlan().getIsExpired()){
                                    SharedPreferencesManager.setUserPaymentIsActive(PaymentActivity.this,true);
                                    status = true;
                                }
                                hideProgressDialog();
                            }
                            getPlans();
                        },
                        e -> {
                            hideProgressDialog();
                            finish();
                        });
        compositeDisposable.add(success);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        if(userMadePayment)
            setResult(Activity.RESULT_OK);
        super.finish();
    }
}
