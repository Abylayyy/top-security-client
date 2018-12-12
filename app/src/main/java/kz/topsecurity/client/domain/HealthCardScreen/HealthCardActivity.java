package kz.topsecurity.client.domain.HealthCardScreen;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.HealthCardScreen.additionalScreen.ListInputActivity;

public class HealthCardActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int ALLERGIC_REACTION_REQUEST_CODE = 79;
//    @BindView(R.id.tv_date_of_birth) TextView tv_date_of_birth;
    @BindView(R.id.tv_edit_date_of_birth) TextView tv_edit_date_of_birth;
    @BindView(R.id.fl_date_of_birth_container) FrameLayout fl_date_of_birth_container;
//    @BindView(R.id.tv_blood_group) TextView tv_blood_group;
    @BindView(R.id.tv_edit_blood_group) TextView tv_edit_blood_group;
    @BindView(R.id.fl_blood_group_container) FrameLayout fl_blood_group_container;
//    @BindView(R.id.tv_weight) TextView tv_weight;
    @BindView(R.id.tv_edit_weight) TextView tv_edit_weight;
    @BindView(R.id.fl_weight_container) FrameLayout fl_weight_container;
//    @BindView(R.id.tv_height) TextView tv_height;
    @BindView(R.id.tv_edit_height) TextView tv_edit_height;
    @BindView(R.id.fl_height_container) FrameLayout fl_height_container;
//    @BindView(R.id.tv_allergic_reaction) TextView tv_allergic_reaction;
    @BindView(R.id.tv_edit_allergic_reaction) TextView tv_edit_allergic_reaction;
    @BindView(R.id.fl_allergic_reaction_container) FrameLayout fl_allergic_reaction_container;
//    @BindView(R.id.tv_drugs) TextView tv_drugs;
    @BindView(R.id.tv_edit_drugs) TextView tv_edit_drugs;
    @BindView(R.id.fl_drugs_container) FrameLayout fl_drugs_container;
//    @BindView(R.id.tv_disease) TextView tv_disease;
    @BindView(R.id.tv_edit_disease) TextView tv_edit_disease;
    @BindView(R.id.fl_disease_container) FrameLayout fl_disease_container;

    String allergicReactionValues = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ALLERGIC_REACTION_REQUEST_CODE: {
                if(resultCode == RESULT_OK && data!=null) {
                    if (data.getStringExtra(ListInputActivity.FIELD_VALUE) != null) {
                        allergicReactionValues = data.getStringExtra(ListInputActivity.FIELD_VALUE);
                        tv_edit_allergic_reaction.setText(allergicReactionValues);
                    }
                }

//                if(data.hasExtra(SHOULD_FINISH) && data.getBooleanExtra(SHOULD_FINISH,false)){
//                    finish();
//                }
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_card);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Мед. карта");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fl_date_of_birth_container.setOnClickListener(this);
        fl_blood_group_container.setOnClickListener(this);
        fl_weight_container.setOnClickListener(this);
        fl_height_container.setOnClickListener(this);
        fl_allergic_reaction_container.setOnClickListener(this);
        fl_drugs_container.setOnClickListener(this);
        fl_disease_container.setOnClickListener(this);

//        ConstraintLayout touchInterceptor = (ConstraintLayout)findViewById(R.id.cl_main_container);
//        touchInterceptor.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    if (et_edit_allergic_reaction.isFocused()) {
//                        Rect outRect = new Rect();
//                        et_edit_allergic_reaction.getGlobalVisibleRect(outRect);
//                        if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
//                            et_edit_allergic_reaction.clearFocus();
//                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                        }
//                    }
//                }
//                return false;
//            }
//        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case android.R.id.home:{
                onBackPressed();
                break;
            }
            case R.id.done_action:{
                saveHealthCard();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveHealthCard() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_email_menu, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fl_date_of_birth_container:{
                showDatePicker();
                break;
            }
            case R.id.fl_blood_group_container:{

                break;
            }
            case R.id.fl_weight_container:{

                break;
            }
            case R.id.fl_height_container:{

                break;
            }
            case R.id.fl_allergic_reaction_container:{
                openAllergicReactionScreen();
                break;
            }
            case R.id.fl_drugs_container:{

                break;
            }
            case R.id.fl_disease_container:{

                break;
            }
        }
    }

    private void openAllergicReactionScreen() {
        Intent intent = new Intent(this, ListInputActivity.class);
        intent.putExtra(ListInputActivity.LABEL_NAME,getString(R.string.allergic_reaction));
        intent.putExtra(ListInputActivity.FIELD_NAME,"allergic_reaction");
        startActivityForResult(intent,ALLERGIC_REACTION_REQUEST_CODE);
    }


    Calendar dateAndTime=Calendar.getInstance();

    private void showDatePicker() {

        DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateAndTime.set(Calendar.YEAR, year);
                dateAndTime.set(Calendar.MONTH, monthOfYear);
                dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        };

        new DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }


}
