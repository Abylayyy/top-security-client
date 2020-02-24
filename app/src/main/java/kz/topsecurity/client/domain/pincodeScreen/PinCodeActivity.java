package kz.topsecurity.client.domain.pincodeScreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.poovam.pinedittextfield.CirclePinField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.MainScreen.AlertActivity;
import kz.topsecurity.client.domain.MainScreen.MainActivityNew;
import kz.topsecurity.client.domain.ProfileScreen.ProfileActivity;
import kz.topsecurity.client.domain.StartScreen.StartActivity;
import kz.topsecurity.client.helper.SharedPreferencesManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.service.trackingService.TrackingService;

public class PinCodeActivity extends AppCompatActivity {

    private static final int SAVE_STATE = 33;
    private static final int LOGIN_STATE = 22;
    private static final int RESET_PIN_CODE = 44;
    @BindView(R.id.oneBtn) TextView one;
    @BindView(R.id.twoBtn) TextView two;
    @BindView(R.id.threeBtn) TextView three;
    @BindView(R.id.fourBtn) TextView four;
    @BindView(R.id.fiveBtn) TextView five;
    @BindView(R.id.sixBtn) TextView six;
    @BindView(R.id.sevenBtn) TextView seven;
    @BindView(R.id.eightBtn) TextView eight;
    @BindView(R.id.nineBtn) TextView nine;
    @BindView(R.id.zeroBtn) TextView zero;
    @BindView(R.id.faceID) ImageView faceID;
    @BindView(R.id.clearBtn) ImageView clearBtn;
    @BindView(R.id.circleField) CirclePinField pinEdit;
    @BindView(R.id.infoText) TextView infoText;
    @BindView(R.id.forgetPin) TextView forgetPin;

    private static final int SAVED_STATE = 11;
    private int currentState;
    private String value = "";
    private String newValue = "";
    private DataBaseManager dataBaseManager;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_code);
        ButterKnife.bind(this);

        String pincode = getIntent().getStringExtra("pin");
        String action = getIntent().getStringExtra("actionPin");

        dataBaseManager = new DataBaseManagerImpl(getApplication());

        forgetPin.setPaintFlags(forgetPin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if (pincode != null) {
            if (pincode.equals("newLogin")) {
                currentState = LOGIN_STATE;
                infoText.setText("Введите пинкод");
                forgetPin.setVisibility(View.VISIBLE);
            }
        } else if (action != null) {
            if (action.equals("resetPin")) {
                infoText.setText("Введите действующий пинкод чтобы сбросить");
                currentState = RESET_PIN_CODE;
                forgetPin.setVisibility(View.INVISIBLE);
            }
        } else {
            currentState = SAVE_STATE;
            forgetPin.setVisibility(View.INVISIBLE);
        }



        Log.d("CURRENT_STATE:", String.valueOf(currentState));

        List<TextView> numbers = new ArrayList<>(Arrays.asList(
                one, two, three, four, five, six, seven, eight, nine, zero
        ));

        for (TextView number : numbers) {
            number.setOnClickListener(v -> {
                pinEdit.append(number.getText());
            });
        }

        clearBtn.setOnClickListener(v -> {
            int length = pinEdit.getText().length();
            if (length > 0) {
                pinEdit.getText().delete(length - 1, length);
            }
        });

        forgetPin.setOnClickListener(v -> showClearDialog());

        pinEdit.setOnTextCompleteListener((text)-> {
            switch(currentState) {
                case SAVE_STATE: {
                    value = pinEdit.getText().toString();
                    new Handler().postDelayed(()-> {
                        currentState = SAVED_STATE;
                        infoText.setText("Повторите пинкод");
                        pinEdit.getText().clear();
                    }, 500);
                    break;
                }
                case SAVED_STATE: {
                    newValue = pinEdit.getText().toString();
                    if (newValue.equals(value)) {
                        SharedPreferencesManager.setUserPinCode(getApplicationContext(), newValue);
                        SharedPreferencesManager.setUserPinExist(getApplicationContext(), 1);
                        Toast.makeText(getApplicationContext(), "Пинкод успешно сохранен!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        infoText.setText("Пинкоды не совпали, введите заново!");
                        infoText.setTextColor(Color.RED);
                        pinEdit.getText().clear();
                    }
                    break;
                }
                case LOGIN_STATE: {
                    String value = SharedPreferencesManager.getUserPincode(getApplicationContext());

                    if (text.equals(value)) {
                        startActivity(new Intent(getApplicationContext(), AlertActivity.class));
                        finishAffinity();
                    } else {
                        pinEdit.setFillerColor(Color.RED);

                        new Handler().postDelayed(()-> {
                            pinEdit.getText().clear();
                            pinEdit.setFillerColor(Color.parseColor("#6D6D6D"));
                        }, 500);

                    }
                    break;
                }
                case RESET_PIN_CODE: {
                    String value = SharedPreferencesManager.getUserPincode(getApplicationContext());
                    if (text.equals(value)) {
                        SharedPreferencesManager.cleanPinCode(getApplication());
                        Toast.makeText(getApplicationContext(), "Пинкод успешно удален!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        infoText.setText("Вы ввели неправильный пинкод, введите заново!");
                        infoText.setTextColor(Color.RED);
                        pinEdit.getText().clear();
                    }
                    break;
                }
            }
            return true;
        });
    }

    private void showClearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Вы действительно хотите сбросить пинкод?");
        builder.setPositiveButton("Ок", (dialog, which) -> {
            dialog.dismiss();
            backToLogin();
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void backToLogin() {
        clearData();
        startActivity(new Intent(getApplication(), StartActivity.class));
        stopService(new Intent(this, TrackingService.class));
        finishAffinity();
    }

    void clearData(){
        SharedPreferencesManager.clearData(getApplication());
        dataBaseManager.dropClientData();
        dataBaseManager.dropDeviceDataTable();
        ProfileActivity.savedBitmap = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
