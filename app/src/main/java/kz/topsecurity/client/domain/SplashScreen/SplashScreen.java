package kz.topsecurity.client.domain.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.MainScreen.AlertActivity;
import kz.topsecurity.client.domain.MainScreen.MainActivityNew;
import kz.topsecurity.client.domain.StartScreen.StartActivity;
import kz.topsecurity.client.domain.pincodeScreen.PinCodeActivity;
import kz.topsecurity.client.helper.SharedPreferencesManager;

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
        if (!SharedPreferencesManager.getUserData(getApplicationContext())) {
            backToMainActivity();
        }
        else {
            if (SharedPreferencesManager.getUserPinExist(getApplicationContext()) == 1) {
                Intent pintent = new Intent(getApplication(), PinCodeActivity.class);
                pintent.putExtra("pin", "newLogin");
                startActivity(pintent);
                finishAffinity();
            }
            else if (!SharedPreferencesManager.getCheckClientAvatar(getApplicationContext())) {
                Intent intent1 = new Intent(this, MainActivityNew.class);
                boolean startMainScreen = getIntent().getBooleanExtra(START_MAIN_SCREEN_KEY, false);
                if (startMainScreen) {
                    intent1.putExtra(StartActivity.START_MAIN_SCREEN_KEY, true);
                }

                runOnUiThread(() -> {
                    startActivity(intent1);
                    System.gc();
                    finish();
                });
            } else {
                startActivity(new Intent(getApplication(), AlertActivity.class));
                finishAffinity();
            }
        }
    }

    private void backToMainActivity() {
        startActivity(new Intent(getApplication(), StartActivity.class));
        finishAffinity();
    }
}
