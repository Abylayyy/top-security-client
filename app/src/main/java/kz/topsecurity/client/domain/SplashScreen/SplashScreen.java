package kz.topsecurity.client.domain.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.MainActivityRedesign;
import kz.topsecurity.client.domain.StartScreen.StartActivity;

public class SplashScreen extends AppCompatActivity {

    public static final String START_MAIN_SCREEN_KEY = "start_main";

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashScreenTheme);
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, StartActivity.class);
        Intent intent1 = new Intent(this, MainActivityRedesign.class);
        boolean startMainScreen = getIntent().getBooleanExtra(START_MAIN_SCREEN_KEY, false);
        if(startMainScreen){
            intent.putExtra(StartActivity.START_MAIN_SCREEN_KEY,true);
        }
        if (StartActivity.isUserLoggedIn) {
            startActivity(intent1);
            System.gc();
            finish();
        } else {
            startActivity(intent);
            System.gc();
            finish();
        }
    }
}
