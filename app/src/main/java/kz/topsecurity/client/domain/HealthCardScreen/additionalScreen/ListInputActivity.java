package kz.topsecurity.client.domain.HealthCardScreen.additionalScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.PlaceScreen.adapter.PlaceListDecorator;
import kz.topsecurity.client.domain.base.HelperActivity;
import kz.topsecurity.client.ui_widgets.customDialog.CustomSimpleDialog;

public class ListInputActivity extends HelperActivity implements ValuesAdapterListener {

    public static final String LABEL_NAME = "LABEL_NAME_EXTRA";
    public static final String FIELD_NAME = "FIELD_NAME_EXTRA";
    public static final String FIELD_VALUE = "FIELD_VALUE_EXTRA";

    @BindView(R.id.rv_values) RecyclerView rv_values;
    @BindView(R.id.tv_label) TextView tv_label;
    @BindView(R.id.ed_text_input) EditText ed_text_input;
    LinearLayoutManager mLayoutManager;

    ValuesAdapter mValuesAdapter = new ValuesAdapter(new ArrayList<>(),this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_input);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String labelName = getIntent().getStringExtra(LABEL_NAME);
        String fieldName = getIntent().getStringExtra(FIELD_NAME);
        String fieldValue = getIntent().getStringExtra(FIELD_VALUE);

        if(labelName==null || labelName.isEmpty()){
            finish();
            return;
        }

        setTitle(labelName);
        tv_label.setText(labelName);

        setupRV();
        if(fieldValue!=null && !fieldValue.isEmpty()){
            String[] split = fieldValue.split(",");
            mValuesAdapter.updateData(split);
        }
        TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                if(s.endsWith(",")){
//                    String value = s.replaceAll("\\s+","");
                    String value = s;
                    addValue(value.substring(0,value.length()-1));
                    ed_text_input.getText().clear();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        ed_text_input.addTextChangedListener(textWatcher);
        ed_text_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.integer.customImeActionId || actionId == EditorInfo.IME_ACTION_NEXT) {
                    addValue(ed_text_input.getText().toString());
                    ed_text_input.getText().clear();
                    return true;
                }
                return false;
            }
        });
    }

    private void addValue(String str) {
        String newValue = str.trim();
        if(newValue!=null && !newValue.isEmpty())
            mValuesAdapter.add(newValue);
        else
            showToast(R.string.field_cant_be_empty);
    }

    private void setupRV() {
        rv_values.setHasFixedSize(true);
        rv_values.addItemDecoration(new PlaceListDecorator(this));
        mLayoutManager = new LinearLayoutManager(this);
        rv_values.setLayoutManager(mLayoutManager);
        rv_values.setAdapter(mValuesAdapter);
    }

    @Override
    public void onItemSelected(int index, String value) {

    }

    @Override
    public void onItemRemove(int index, String value) {
        mValuesAdapter.removeByDataId(index);
    }

    @Override
    public void showToastMsg(int already_added) {
        showToast(already_added);
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
                saveValue();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
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

    private void saveValue() {
        if(!ed_text_input.getText().toString().isEmpty()){
            addValue(ed_text_input.getText().toString());
            ed_text_input.getText().clear();
        }
        StringBuilder value = new StringBuilder();
        List<String> data = mValuesAdapter.getData();
        for (int i =0 ; i<data.size() ; i++) {
            value.append(data.get(i));
            if(i != data.size()-1) value.append(",");
        };
        Intent intent = new Intent();
        intent.putExtra(FIELD_VALUE, value.toString());
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_email_menu, menu);
        return true;
    }
}
