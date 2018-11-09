package kz.topsecurity.client.domain.PaymentScreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.model.auth.GetClientResponse;
import kz.topsecurity.client.model.other.BasicResponseTemplate;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;

public class PaymentActivity extends BaseActivity {

    public static final String FORCED = "FORCED_PAYMENT";
    private static final int FIRST_CHECK = 1144;
    private static final int LAST_CHECK = 759;
    @BindView(R.id.wv_payment_site)
    WebView wv_payment_site;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.payment_activity_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
                checkPlan(LAST_CHECK);
                return false;
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
                            } else {
                                if(r.getClient().getPlan()!=null && !r.getClient().getPlan().getIsExpired()){
                                    SharedPreferencesManager.setUserPaymentIsActive(PaymentActivity.this,true);
                                    status = true;
                                }
                                hideProgressDialog();
                            }
                            if(type==FIRST_CHECK){
                                if(status) {
                                    showToast(R.string.you_already_paid);
                                    finish();
                                }else
                                    getPlans();
                            }
                            else if(type == LAST_CHECK){
                                finish();
                            }
                        },
                        e -> {
                            hideProgressDialog();
                            finish();
                        });
        compositeDisposable.add(success);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
