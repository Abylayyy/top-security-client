package kz.topsecurity.client.service.api;

import java.util.concurrent.TimeUnit;

import kz.topsecurity.client.application.TopSignalApplication;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://gpstracking.muratov.kz/api/";

    private static Retrofit getRetrofitInstance() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10,TimeUnit.SECONDS).addInterceptor(interceptor).build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getClientApi(){
        return getRetrofitInstance().create(ApiService.class);
    }

    public static String getRequestToken(){
        return "Bearer " + getRawToken();
    }

    public static String getRawToken(){
        return SharedPreferencesManager.getUserAuthToken(TopSignalApplication.getInstance().getApplicationContext());
    }
}
