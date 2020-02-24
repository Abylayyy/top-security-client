package kz.topsecurity.client.fragments.main_fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.HealthCardScreen.NumberChooserAdapter;
import kz.topsecurity.client.domain.HealthCardScreen.SliderLayoutManagerNew;
import kz.topsecurity.client.domain.MainScreen.MainActivityNew;
import kz.topsecurity.client.fragments.main_fragment.main_adapter.ContactAdapter;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.auth.GetClientResponse;
import kz.topsecurity.client.model.contact.Contact;
import kz.topsecurity.client.model.contact.GetContactsResponse;
import kz.topsecurity.client.model.contact.SaveContactsResponse;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.model.other.HealthCardPostResponse;
import kz.topsecurity.client.model.other.Healthcard;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextWithPhoneMask;
import kz.topsecurity.client.utils.GlideApp;

public class MedicineFragment extends Fragment {

    @BindView(R.id.med_date) TextView medDate;
    @BindView(R.id.medGroup) TextView medGroup;
    @BindView(R.id.medWeight) EditText medWeight;
    @BindView(R.id.medHeight) EditText medHeight;
    @BindView(R.id.medAllergy) EditText medAll;
    @BindView(R.id.medMedik) EditText medMedik;
    @BindView(R.id.medZabo) EditText medZabo;
    @BindView(R.id.addNewCon) TextView addNewCon;
    @BindView(R.id.changeMedInfo) TextView medInfo;
    @BindView(R.id.profPageImage)
    CircleImageView profImage;
    @BindView(R.id.view) View v1;
    @BindView(R.id.view2) View v2;
    @BindView(R.id.view3) View v3;
    @BindView(R.id.view4) View v4;
    @BindView(R.id.view5) View v5;
    @BindView(R.id.view8) View v8;
    @BindView(R.id.view9) View v9;
    @BindView(R.id.view10) View v10;
    @BindView(R.id.phoneRecycler) RecyclerView phoneRecycler;

    View[] array;
    EditText[] lol;
    View[] view_array, gol;
    ContactAdapter adapter;
    List<Contact> myList;
    private String uri;
    Dialog dialog;
    EditText name, who;
    RoundCorneredEditTextWithPhoneMask phoneMask;
    Button save, cancel;
    View nameView, whoView, phoneView;
    private DataBaseManager dataBaseManager;
    private String dateOfBirth = "";
    private int user_blood_type_position = -1;

    SliderLayoutManagerNew mLayoutManager;
    NumberChooserAdapter mAdapter = new NumberChooserAdapter(new ArrayList<>());
    Calendar dateAndTime = Calendar.getInstance();
    long minDate;

    MedicineCallBack mCallback;

    public interface MedicineCallBack {
        void showToast(String msg);
        void onMedInfoCorrect(String blood, String data, String weight, String height, String allergy, String med, String zabo);
        void onSaveMedInfo();
    }

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MedicineFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (MedicineFragment.MedicineCallBack) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicine, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.add_contact_dialog);
        initialize(dialog);

        dataBaseManager = new DataBaseManagerImpl(getActivity());

        setClientAvatar();

        array = new View[] {medDate, medGroup, medWeight, medHeight, medAll, medMedik, medZabo};
        view_array = new View[] {v1, v2, v3, v4, v5, v8, v9, v10};
        gol = new View[] {nameView, whoView, phoneView};
        lol = new EditText[] {name, who, phoneMask};
        myList = new ArrayList<>();

        setUserHealthCardData();

        getTrustedContacts();
        setMinDate();

        addNewCon.setOnClickListener(v -> dialog.show());

        save.setOnClickListener(v -> createContactRequest(name.getText().toString(), who.getText().toString(), phoneMask.getText().toString()));

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
            clear();
            for (View v1 : gol) {
                setColor(v1, "b6b6b6");
            }
        });

        medInfo.setOnClickListener(v -> {
            if (medInfo.getText().equals("Редактировать")) {
                changeMedInfo();
            } else {
                saveAllInfo();
            }
        });

        medGroup.setOnClickListener(v -> showBottomSheetDialog());
        medDate.setOnClickListener(v -> showDatePicker());

        for (int i = 0; i < lol.length; i++) {
            int index = i;
            lol[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    setColor(gol[index], "b6b6b6");
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(minDate);
        datePickerDialog.show();
    }

    DatePickerDialog.OnDateSetListener d = (view, year, monthOfYear, dayOfMonth) -> {
        dateAndTime.set(Calendar.YEAR, year);
        dateAndTime.set(Calendar.MONTH, monthOfYear);
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateBirthDayDate();
    };

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

    private void updateBirthDayDate() {
        String format1 = format.format(dateAndTime.getTime());
        medDate.setText(format1);
        dateOfBirth = format1;
    }

    void setMinDate(){
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add( Calendar.YEAR, -3 );
        minDate = c.getTime().getTime();
    }

    private boolean saveHealthCard() {
        boolean saved = false;
        if(dateOfBirth.isEmpty() || user_blood_type_position == -1 ||
            medAll.getText().toString().isEmpty() || medMedik.getText().toString().isEmpty() ||
            medZabo.getText().toString().isEmpty()){
            Toasty.info(getActivity(), getString(R.string.field_cant_be_empty), Toasty.LENGTH_LONG).show();
        } else {
            String u_w = medWeight.getText().toString();
            String u_h = medHeight.getText().toString();
            String u_b = getServerBloodValue(user_blood_type_position);
            mCallback.onMedInfoCorrect(u_b, dateOfBirth, u_w, u_h, medAll.getText().toString(), medMedik.getText().toString(), medZabo.getText().toString());
            mCallback.onSaveMedInfo();
            saved = true;
        }
        return saved;
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

    private void setClientAvatar() {
        DataBaseManager dataBaseManager = new DataBaseManagerImpl(getActivity());
        String userAvatar = null;
        Client clientData = dataBaseManager.getClientData();

        if(clientData!=null){
            userAvatar = clientData.getPhoto();
        }
        GlideApp.with(getActivity())
                .load(userAvatar)
                .placeholder(R.drawable.big_prof_image)
                .into(profImage);
    }

    private void clear() {
        name.getText().clear();
        who.getText().clear();
        phoneMask.getText().clear();
    }

    private void initialize(Dialog dialog) {
        name = dialog.findViewById(R.id.addName);
        who = dialog.findViewById(R.id.addWho);
        phoneMask = dialog.findViewById(R.id.addPhone);
        save = dialog.findViewById(R.id.saveBtn);
        cancel = dialog.findViewById(R.id.cancelBtn);
        nameView = dialog.findViewById(R.id.nameView);
        whoView = dialog.findViewById(R.id.whoView);
        phoneView = dialog.findViewById(R.id.phoneView);
    }

    private boolean checkContact(Contact contact) {
        boolean check = false;
        boolean[] arr = {false, false, false};

        if (contact.getName().equals("")) { setColor(nameView, "EF3B39"); } else {
            setColor(nameView, "b6b6b6"); arr[0] = true; }

        if (contact.getDescription().equals("")) { setColor(whoView, "EF3B39"); } else {
            setColor(whoView, "b6b6b6"); arr[1] = true; }

        if (contact.getPhone().length() != 16) { setColor(phoneView, "EF3B39"); } else {
            setColor(phoneView, "b6b6b6"); arr[2] = true; }

        if (arr[0] && arr[1] && arr[2]) {
            check = true;
        }
        return check;
    }

    private void setColor(View view, String color) {
        view.setBackgroundColor(Color.parseColor("#" + color));
    }

    private void setData(Context context, List<Contact> list) {
        adapter = new ContactAdapter(context, list);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        phoneRecycler.setLayoutManager(manager);
        phoneRecycler.setItemAnimator(new DefaultItemAnimator());
        phoneRecycler.setAdapter(adapter);
        phoneRecycler.setHasFixedSize(true);
        adapter.notifyDataSetChanged();
    }


    public void getTrustedContacts() {
        Disposable success = new RequestService<>(new RequestService.RequestResponse<GetContactsResponse>() {
            @Override
            public void onSuccess(GetContactsResponse r) {
                if(r.getContacts()==null){
                    Toast.makeText(getContext(), "Empty list!", Toast.LENGTH_SHORT).show();
                }
                else{
                    myList.addAll(r.getContacts());
                    Collections.reverse(myList);
                    setData(getContext(), myList);
                }
            }

            @Override
            public void onFailed(GetContactsResponse data, int error_message) {
                Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e, int error_message) {
                Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .getContacts(RetrofitClient.getRequestToken()));

        compositeDisposable.add(success);
    }

    private void updateDatabase() {
        Disposable disposable = new RequestService<>(new RequestService.RequestResponse<GetClientResponse>() {
            @Override
            public void onSuccess(GetClientResponse data) {
                dataBaseManager.saveClientData(data.getClient());
            }
            @Override
            public void onFailed(GetClientResponse data, int error_message) {}
            @Override
            public void onError(Throwable e, int error_message) {}

        }).makeRequest(RetrofitClient.getClientApi().getClientData(RetrofitClient.getRequestToken()));
        compositeDisposable.add(disposable);
    }

    private void createContactRequest(String username , String description, String phone) {
        if (checkContact(new Contact(username, description, phone))) {
            Disposable success = new RequestService<>(new RequestService.RequestResponse<SaveContactsResponse>() {
                @Override
                public void onSuccess(SaveContactsResponse r) {
                    Contact contact = r.getContact();
                    adapter.add(contact);
                    clear();
                    dialog.dismiss();
                }

                @Override
                public void onFailed(SaveContactsResponse data, int error_message) {
                    showDialog("Не удалось добавить контакт");
                }

                @Override
                public void onError(Throwable e, int error_message) {
                    showDialog("Ошибка при добавлении контакта");
                }
            }).makeRequest(RetrofitClient.getClientApi()
                    .saveContact(RetrofitClient.getRequestToken(), username, phone, description));

            compositeDisposable.add(success);
        } else {
            return;
        }
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message);
        builder.setPositiveButton("ОК", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void saveAllInfo() {
        if (saveHealthCard()) {
            medInfo.setText("Редактировать");
            medInfo.setTextColor(Color.parseColor("#1F9900"));
            for (View view : array) {
                view.setEnabled(false);
            }
            for (View text : view_array) {
                text.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void changeMedInfo() {
        medInfo.setText("Сохранить");
        medInfo.setTextColor(Color.parseColor("#EF3B39"));
        for (View view : array) {
            view.setEnabled(true);
        }
        for (View text : view_array) {
            text.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setUserHealthCardData() {
        updateDatabase();
        new Handler().postDelayed(()-> {
            Healthcard healthcard = dataBaseManager.getClientData().getHealthcard();
            if(healthcard.getBirthday()!= null && !healthcard.getBirthday().isEmpty()) {
                medDate.setText(healthcard.getBirthday());
                setDateOfBirth(healthcard.getBirthday());
            }
            if(healthcard.getBloodGroup()!= null && !healthcard.getBloodGroup().isEmpty()) {
                setBloodGroup(healthcard.getBloodGroup());
                if(user_blood_type_position != 1)
                    medGroup.setText(addBloodDataToAdapter().get(user_blood_type_position));
            }
            if(healthcard.getWeight()!= null && healthcard.getWeight() > 2.0f) {
                medWeight.setText(healthcard.getWeight().toString() + " кг");
            }
            if(healthcard.getHeight()!= null && healthcard.getHeight() > 2.0f) {
                medHeight.setText(healthcard.getHeight().toString() + " см");
            }
            if(healthcard.getAllergicReactions()!= null && !healthcard.getAllergicReactions().isEmpty()) {
                medAll.setText(healthcard.getAllergicReactions());
            }
            if(healthcard.getDrugs()!= null && !healthcard.getDrugs().isEmpty()){
                medMedik.setText(healthcard.getDrugs());
            }
            if(healthcard.getDisease()!= null && !healthcard.getDisease().isEmpty()){
                medZabo.setText(healthcard.getDisease());
            }
        }, 300);
    }

    private void setBloodGroup(String bloodGroup) {
        this.user_blood_type_position = getServerBloodIndex(bloodGroup);
    }

    private List<String> addBloodDataToAdapter() {
        return new ArrayList<>(Arrays.asList("I", "I+", "II", "II+", "III" , "III+","IV","IV+"));
    }

    private int getServerBloodIndex(String str) {
        return Arrays.asList("a_positive",
                "a_negative",
                "b_positive",
                "b_negative",
                "c_positive",
                "c_negative",
                "d_positive",
                "d_negative").indexOf(str);
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

    private void setStartElement(RecyclerView rv_number_list) {
        if (user_blood_type_position != -1) {
            rv_number_list.scrollToPosition(user_blood_type_position);
            rv_number_list.smoothScrollToPosition(user_blood_type_position);
        }
        else {
            rv_number_list.scrollToPosition(1);
            rv_number_list.smoothScrollToPosition(1);
        }
    }

    private void showBottomSheetDialog(){
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity());
        View sheetView = getLayoutInflater().inflate(R.layout.fragment_number_chooser_bottom_sheet, null);
        RecyclerView rv_number_list = sheetView.findViewById(R.id.rv_number_list);
        TextView bottomSheetLabel = sheetView.findViewById(R.id.tv_label);
        TextView tv_value = sheetView.findViewById(R.id.tv_value);
        setupRV(rv_number_list);
        List<String> dataList;
        dataList = (addBloodDataToAdapter());
        bottomSheetLabel.setText(getString(R.string.blood_group));

        mAdapter.updateData(dataList);
        List<String> finalDataList = dataList;
        mLayoutManager.setCallback(layoutPosition -> {
            String aFloat = finalDataList.get(layoutPosition);
            String value_text = "";
            value_text = String.format("%s",aFloat);
            medGroup.setText(value_text);
            user_blood_type_position = layoutPosition;
            tv_value.setText(value_text);
        });

        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.setOnDismissListener(dialog -> {
            String test = "";
        });
        mBottomSheetDialog.show();
        setStartElement(rv_number_list);
    }

    private void setupRV(RecyclerView rv_numberList) {
        mLayoutManager = new SliderLayoutManagerNew(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        int padding = getScreenWidth()/2;
        rv_numberList.setPadding(padding, 0, padding, 0);
        rv_numberList.setLayoutManager(mLayoutManager);
        rv_numberList.setAdapter(mAdapter);
    }

    int getScreenWidth(){
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(dm);
        }
        return dm.widthPixels;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
