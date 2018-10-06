package kz.topsecurity.client.domain.SplashScreen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.StartScreen.StartActivity;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.introductionScreen.IntroductionActivity;

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

        if(Constants.DEVELOP_MODE)
            startActivity(new Intent(this,IntroductionActivity.class));

        Intent intent = new Intent(this, StartActivity.class);
        boolean startMainScreen = getIntent().getBooleanExtra(START_MAIN_SCREEN_KEY, false);
        if(startMainScreen){
            intent.putExtra(StartActivity.START_MAIN_SCREEN_KEY,true);
            return;
        }
        startActivity(intent);
        finish();
    }
}
