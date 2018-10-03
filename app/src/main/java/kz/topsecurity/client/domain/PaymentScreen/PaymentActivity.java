package kz.topsecurity.client.domain.PaymentScreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;

public class PaymentActivity extends BaseActivity {

    @BindView(R.id.wv_payment_site)
    WebView wv_payment_site;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.payment_activity_name);

        WebSettings webSettings = wv_payment_site.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCacheMaxSize(100 * 1000 * 1000);

        wv_payment_site.setWebChromeClient(new WebChromeClient());
        wv_payment_site.setWebViewClient(new PaymentWebViewClient());
        wv_payment_site.loadUrl(getUrlPath());
    }

    private String getUrlPath() {
        return "http://gpstracking.muratov.kz/client/plans?access_token="+RetrofitClient.getRawToken();
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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            return false;
        }
    }
}
