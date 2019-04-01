package kz.topsecurity.client.domain.HealthCardScreen;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.HealthCardScreen.additionalScreen.ListInputActivity;
import kz.topsecurity.client.domain.HealthCardScreen.additionalScreen.SliderLayoutManagerNew;
import kz.topsecurity.client.domain.base.HelperActivity;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.other.BasicResponse;
import kz.topsecurity.client.model.other.HealthCardPostResponse;
import kz.topsecurity.client.model.other.Healthcard;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.ui_widgets.customDialog.CustomSimpleDialog;

public class HealthCardActivity extends HelperActivity implements View.OnClickListener {

    private static final int TYPE_WEIGHT = 1;
    private static final int TYPE_HEIGHT = 2;
    private static final int TYPE_BLOOD = 3;

    private static final int ALLERGIC_REACTION_REQUEST_CODE = 79;
    private static final int DRUGS_REQUEST_CODE = 51;
    private static final int DISEASES_REQUEST_CODE = 87;

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

    private String dateOfBirth = "";
    private int user_weight_position = -1;
    private int user_height_position = -1;
    private int user_blood_type_position = -1;
    private String allergicReactionValues = "";
    private String drugsValues ="";
    private String diseaseValues ="";
    Context context;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String stringExtra ="";
        if(data!=null)
            stringExtra = data.getStringExtra(ListInputActivity.FIELD_VALUE);
        switch (requestCode) {
            case ALLERGIC_REACTION_REQUEST_CODE: {
                if(resultCode == RESULT_OK && data!=null) {
                    if (!stringExtra.isEmpty()) {
                        allergicReactionValues = stringExtra;
                        tv_edit_allergic_reaction.setText(allergicReactionValues);
                    }
                    else{
                        allergicReactionValues = "";
                        tv_edit_allergic_reaction.setText(R.string.empty_field);
                    }
                }

                break;
            }
            case DRUGS_REQUEST_CODE: {
                if(resultCode == RESULT_OK && data!=null) {
                    if (!stringExtra.isEmpty()) {
                        drugsValues = stringExtra;
                        tv_edit_drugs.setText(drugsValues);
                    }
                    else{
                        drugsValues = "";
                        tv_edit_drugs.setText(R.string.empty_field);
                    }
                }
                break;
            }
            case DISEASES_REQUEST_CODE: {
                if(resultCode == RESULT_OK && data!=null) {
                    if (!stringExtra.isEmpty()) {
                        diseaseValues = stringExtra;
                        tv_edit_disease.setText(diseaseValues);
                    }
                    else{
                        diseaseValues = "";
                        tv_edit_disease.setText(R.string.empty_field);
                    }
                }
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
        setMinDate();
        fl_date_of_birth_container.setOnClickListener(this);
        fl_blood_group_container.setOnClickListener(this);
        fl_weight_container.setOnClickListener(this);
        fl_height_container.setOnClickListener(this);
        fl_allergic_reaction_container.setOnClickListener(this);
        fl_drugs_container.setOnClickListener(this);
        fl_disease_container.setOnClickListener(this);
        setUserHealthCardData();
        context = this;
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

    DataBaseManager dataBaseManager = new DataBaseManagerImpl(this);

    private void setUserHealthCardData() {
        Healthcard healthcard = dataBaseManager.getClientData().getHealthcard();
        if(healthcard.getBirthday()!=null && !healthcard.getBirthday().isEmpty()) {
            tv_edit_date_of_birth.setText(healthcard.getBirthday());
            setDateOfBirth(healthcard.getBirthday());
        }
        else
            tv_edit_date_of_birth.setText(getString(R.string.empty_field));
        if(healthcard.getBloodGroup()!=null && !healthcard.getBloodGroup().isEmpty()) {
            setBloodGroup(healthcard.getBloodGroup());
            if(user_blood_type_position!=1)
                tv_edit_blood_group.setText(addBloodDataToAdapter().get(user_blood_type_position));
            else
                tv_edit_blood_group.setText(getString(R.string.empty_field));
        }
        else
            tv_edit_blood_group.setText(getString(R.string.empty_field));
        if(healthcard.getWeight()!=null && healthcard.getWeight()>2.0f) {
            tv_edit_weight.setText(healthcard.getWeight().toString());
            setWeight(healthcard.getWeight());
        }
        else
            tv_edit_weight.setText(getString(R.string.empty_field));
        if(healthcard.getHeight()!=null && healthcard.getHeight()>2.0f) {
            tv_edit_height.setText(healthcard.getHeight().toString());
            setHeight(healthcard.getHeight());
        }
        else
            tv_edit_height.setText(getString(R.string.empty_field));
        if(healthcard.getAllergicReactions()!=null && !healthcard.getAllergicReactions().isEmpty()) {
            tv_edit_allergic_reaction.setText(healthcard.getAllergicReactions());
            allergicReactionValues = healthcard.getAllergicReactions();
        }
        else
            tv_edit_allergic_reaction.setText(getString(R.string.empty_field));
        if(healthcard.getDrugs()!=null && !healthcard.getDrugs().isEmpty()){
            tv_edit_drugs.setText(healthcard.getDrugs());
            drugsValues = healthcard.getDrugs();
        }
        else
            tv_edit_drugs.setText(getString(R.string.empty_field));
        if(healthcard.getDisease()!=null && !healthcard.getDisease().isEmpty()){
            tv_edit_disease.setText(healthcard.getDisease());
            diseaseValues = healthcard.getDisease();
        }
        else
            tv_edit_disease.setText(getString(R.string.empty_field));
    }

    private void setHeight(Float height) {
        this.user_height_position =addHeightDataToAdapter().indexOf(height.toString());
    }

    private void setWeight(Float weight) {
        this.user_weight_position =addWeightDataToAdapter().indexOf(weight.toString());
    }

    private void setBloodGroup(String bloodGroup) {
        this.user_blood_type_position = getServerBloodIndex(bloodGroup.toString());
    }

    private void setDateOfBirth(String birthday) {
        dateOfBirth = birthday;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date = null;
        try {
            date = sdf.parse(dateOfBirth);
            dateAndTime.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        showAreYouSureDialog(getString(R.string.are_you_sure_without_saving_data), new CustomSimpleDialog.Callback() {
            @Override
            public void onCancelBtnClicked() {
                dissmissAreYouSureDialog();
            }

            @Override
            public void onPositiveBtnClicked() {
                dissmissAreYouSureDialog();
                finish();
            }
        });
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
        if(dateOfBirth.isEmpty() || user_weight_position==-1 ||
                user_height_position == -1 || user_blood_type_position == -1 ||
                allergicReactionValues.isEmpty() || drugsValues.isEmpty() ||
                diseaseValues.isEmpty()){
            showToast(getString(R.string.field_cant_be_empty));
            return;
        }
        else{
            showLoadingDialog();
            String u_w = addHeightDataToAdapter().get(user_weight_position);
            String u_h = addHeightDataToAdapter().get(user_height_position);
            String u_b = getServerBloodValue(user_blood_type_position);

            Disposable disposable = new RequestService<HealthCardPostResponse>(new RequestService.RequestResponse<HealthCardPostResponse>() {


                @Override
                public void onSuccess(HealthCardPostResponse data) {
                    hideProgressDialog();
                    if(data.getHealthcard()!=null) {
                        dataBaseManager.updateHealthCard(data.getHealthcard(),context);
                    }

                    closeActivity();
                }

                @Override
                public void onFailed(HealthCardPostResponse data, int error_message) {
                    hideProgressDialog();
                    showToast(error_message);
                }

                @Override
                public void onError(Throwable e, int error_message) {
                    hideProgressDialog();
                    showToast(error_message);
                }
            }).makeRequest(RetrofitClient.getClientApi().addUserHealthCard( RetrofitClient.getRequestToken(), u_b, dateOfBirth, u_w, u_h, allergicReactionValues, drugsValues, diseaseValues));
            compositeDisposable.add(disposable);

        }
    }

    private void closeActivity() {
        setResult(RESULT_OK);
        finish();
    }

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
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
                showBottomSheetDialog(TYPE_BLOOD);
                break;
            }
            case R.id.fl_weight_container:{
                showBottomSheetDialog(TYPE_WEIGHT);
                break;
            }
            case R.id.fl_height_container:{
                showBottomSheetDialog( TYPE_HEIGHT);
                break;
            }
            case R.id.fl_allergic_reaction_container:{
                openAllergicReactionScreen();
                break;
            }
            case R.id.fl_drugs_container:{
                openDrugsScreen();
                break;
            }
            case R.id.fl_disease_container:{
                openDiseaseScreen();
                break;
            }
        }
    }

    private void openAllergicReactionScreen() {
        Intent intent = new Intent(this, ListInputActivity.class);
        intent.putExtra(ListInputActivity.LABEL_NAME,getString(R.string.allergic_reaction));
        intent.putExtra(ListInputActivity.FIELD_NAME,"allergic_reaction");
        intent.putExtra(ListInputActivity.FIELD_VALUE,allergicReactionValues);
        startActivityForResult(intent,ALLERGIC_REACTION_REQUEST_CODE);
    }

    private void openDrugsScreen() {
        Intent intent = new Intent(this, ListInputActivity.class);
        intent.putExtra(ListInputActivity.LABEL_NAME,getString(R.string.drugs));
        intent.putExtra(ListInputActivity.FIELD_NAME,"drugs");
        intent.putExtra(ListInputActivity.FIELD_VALUE, drugsValues);
        startActivityForResult(intent,DRUGS_REQUEST_CODE);
    }


    private void openDiseaseScreen() {
        Intent intent = new Intent(this, ListInputActivity.class);
        intent.putExtra(ListInputActivity.LABEL_NAME,getString(R.string.disease));
        intent.putExtra(ListInputActivity.FIELD_NAME,"disease");
        intent.putExtra(ListInputActivity.FIELD_VALUE, diseaseValues);
        startActivityForResult(intent,DISEASES_REQUEST_CODE);
    }

    Calendar dateAndTime=Calendar.getInstance();

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateBirthDayDate();
        }
    };

    void setMinDate(){
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add( Calendar.YEAR, -3 );
        minDate = c.getTime().getTime();
    }

    long minDate;

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(minDate);
        datePickerDialog.show();
    }

    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    private void updateBirthDayDate() {
        String format1 = format.format(dateAndTime.getTime());
        tv_edit_date_of_birth.setText(format1);
        dateOfBirth = format1;
    }

    SliderLayoutManagerNew mLayoutManager;
    NumberChooserAdapter mAdapter = new NumberChooserAdapter(new ArrayList<>());

    void showBottomSheetDialog(int type){
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.fragment_number_chooser_bottom_sheet, null);
        RecyclerView rv_number_list = (RecyclerView) sheetView.findViewById(R.id.rv_number_list);
        TextView bottomSheetLabel = (TextView) sheetView.findViewById(R.id.tv_label);
        ImageView iv_close = (ImageView) sheetView.findViewById(R.id.iv_close);
        TextView tv_value = sheetView.findViewById(R.id.tv_value);
        setupRV(rv_number_list);
        List<String> dataList = new ArrayList<>();
        if(type == TYPE_WEIGHT) {
            dataList = (addWeightDataToAdapter());
            bottomSheetLabel.setText(getString(R.string.weight));
        }else if(type == TYPE_HEIGHT)
        {
            dataList = (addHeightDataToAdapter());
            bottomSheetLabel.setText(getString(R.string.height));
        }else if(type == TYPE_BLOOD)
        {
            dataList = (addBloodDataToAdapter());
            bottomSheetLabel.setText(getString(R.string.blood_group));
        }

        mAdapter.updateData(dataList);
        List<String> finalDataList = dataList;
        mLayoutManager.setCallback(layoutPosition -> {
            String aFloat = finalDataList.get(layoutPosition);
            String value_text = "";
            if(type == TYPE_WEIGHT) {
                value_text = String.format("%s кг",aFloat);
                tv_edit_weight.setText(value_text);
                user_weight_position = layoutPosition;
            }
            else if(type == TYPE_HEIGHT)
            {
                value_text = String.format("%s см",aFloat);
                tv_edit_height.setText(value_text);
                user_height_position = layoutPosition;
            }
            else if(type == TYPE_BLOOD)
            {
                value_text = String.format("%s",aFloat);
                tv_edit_blood_group.setText(value_text);
                user_blood_type_position = layoutPosition;
            }
            tv_value.setText(value_text);
        });

        iv_close.setOnClickListener(v->{
            mBottomSheetDialog.dismiss();
        });
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String test = "";
            }
        });
        mBottomSheetDialog.show();
        setStartElement(rv_number_list, type);

    }

    private void setStartElement(RecyclerView rv_number_list, int type) {
        if (type == TYPE_WEIGHT) {
            if (user_weight_position != -1){
                rv_number_list.scrollToPosition(user_weight_position);
                rv_number_list.smoothScrollToPosition(user_weight_position);
            }
            else{
                rv_number_list.scrollToPosition(116);
                rv_number_list.smoothScrollToPosition(116);
            }
        } else if (type == TYPE_HEIGHT) {
            if (user_height_position != -1){
                rv_number_list.scrollToPosition(user_height_position);
                rv_number_list.smoothScrollToPosition(user_height_position);
            }
            else {
                rv_number_list.scrollToPosition(340);
                rv_number_list.smoothScrollToPosition(340);
            }
        }else if (type == TYPE_BLOOD) {
            if (user_blood_type_position != -1) {
                rv_number_list.scrollToPosition(user_blood_type_position);
                rv_number_list.smoothScrollToPosition(user_blood_type_position);
            }
            else {
                rv_number_list.scrollToPosition(1);
                rv_number_list.smoothScrollToPosition(1);
            }
        }
    }

    private String getServerBloodValue(int i){
        return Arrays.asList("a_positive",
                "a_negative",
                "b_positive",
                "b_negative",
                "c_positive",
                "c_negative",
                "d_positive",
                "d_negative").get(i);
    }

    private int getServerBloodIndex(String str){
        return Arrays.asList("a_positive",
                "a_negative",
                "b_positive",
                "b_negative",
                "c_positive",
                "c_negative",
                "d_positive",
                "d_negative").indexOf(str);
    }


    private List<String> addHeightDataToAdapter() {
        List<String> dataList = new ArrayList<>();
        for (int i=4 ; i<600;i++ ){
            dataList.add(String.valueOf(((float)i)/2));
        }
        return dataList;
    }

    private List<String> addBloodDataToAdapter() {
        return new ArrayList<>(Arrays.asList("I", "I+", "II", "II+", "III" , "III+","IV","IV+"));
    }

    private List<String> addWeightDataToAdapter() {
        List<String> dataList = new ArrayList<>();
        for (int i=4 ; i<1000;i++ ){
            dataList.add(String.valueOf(((float)i)/2));
        }
        return dataList;
    }

    private void setupRV(RecyclerView rv_numberList) {
        mLayoutManager = new SliderLayoutManagerNew(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        int padding = getScreenWidth()/2;
        rv_numberList.setPadding(padding, 0, padding, 0);
//        SnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(rv_numberList);
        rv_numberList.setLayoutManager(mLayoutManager);
        rv_numberList.setAdapter(mAdapter);
    }

    int getScreenWidth(){
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(dm);
        }
        return dm.widthPixels;
    }

    int dpToPx(Context context, int value) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,  (float) value, context.getResources().getDisplayMetrics());
    }
}
