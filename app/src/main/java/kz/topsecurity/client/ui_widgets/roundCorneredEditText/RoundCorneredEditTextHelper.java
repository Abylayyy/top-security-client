package kz.topsecurity.client.ui_widgets.roundCorneredEditText;

import android.app.Activity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

public class RoundCorneredEditTextHelper {

    private RoundCorneredEditText roundCorneredEditText;
    private TextView labelTextView;
    private TextView errorTextView;
    private boolean is_error_textview_visible= true;
    private boolean isStatusError = false;
    private StatusListener listener;

    public RoundCorneredEditTextHelper(StatusListener listener, RoundCorneredEditText editText, TextView labelTextView, TextView errorTextView){
        this.roundCorneredEditText = editText;
        this.labelTextView = labelTextView;
        this.errorTextView = errorTextView;
        this.listener = listener;
    }
    public RoundCorneredEditTextHelper(StatusListener listener, RoundCorneredEditText editText , TextView errorTextView){
        this.roundCorneredEditText = editText;
        this.errorTextView = errorTextView;
        this.listener = listener;
    }
    public void setMandatory(){
        String labelText = labelTextView.getText().toString();
        String text = String.format("%s%s",labelText,"<font color='red'>*</font>");
        labelTextView.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
    }

    public void setError(String msg){
        is_error_textview_visible = true;
        isStatusError = true;
        roundCorneredEditText.setError();
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(msg);
    }

    public void setError(){
        is_error_textview_visible = true;
        isStatusError = true;
        roundCorneredEditText.setError();
        errorTextView.setVisibility(View.VISIBLE);
    }

    public void init(final Activity context){
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(is_error_textview_visible) {
                            errorTextView.setVisibility(View.GONE);
                            is_error_textview_visible = false;
                            errorDeactivated();
                        }
                    }
                });

            }
        };
        roundCorneredEditText.addTextChangedListener(textWatcher);
    }

    void errorDeactivated(){
        roundCorneredEditText.removeError();
        listener.onErrorDeactivated();
    }
}
