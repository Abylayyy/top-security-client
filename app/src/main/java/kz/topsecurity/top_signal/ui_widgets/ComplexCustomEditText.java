package kz.topsecurity.top_signal.ui_widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import kz.topsecurity.top_signal.R;

public class ComplexCustomEditText extends RelativeLayout {

    View rootView;
    EditText valueEditTextView;
    TextView editTextLabel;
    TextView editTextErrorLabel;
    int currentState = State.EMPTY;
    String textHint;
    String textLabel;
    String textError;
    boolean isMandatory = false;
    boolean isEditTextFocused = false;

    Handler handler = new Handler();

    private static class State{
        final static int EMPTY = 0;
        final static int FOCUSED = 1;
        final static int NOT_FOCUSED = 2;

    }

    public ComplexCustomEditText(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ComplexCustomEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ComplexCustomEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ComplexCustomEditText, defStyleAttr, 0);

        textHint = a.getString(R.styleable.ComplexCustomEditText_ed_hint);;
        textLabel= a.getString(R.styleable.ComplexCustomEditText_label_text);;
        textError= a.getString(R.styleable.ComplexCustomEditText_error_text);;
        isMandatory = a.getBoolean(R.styleable.ComplexCustomEditText_is_mandatory,false);;

        rootView = inflate(context, R.layout.template_for_custom_edit_text, this);
        valueEditTextView = (EditText) rootView.findViewById(R.id.valueEditTextView);

        editTextLabel = (TextView)rootView.findViewById(R.id.valueLabel);
        editTextErrorLabel = (TextView) rootView.findViewById(R.id.valueErrorLabel);
        valueEditTextView.requestFocus();
//set focus listener to handle keyboard display
        final Context tmp_ctx = context;
        valueEditTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager)tmp_ctx.getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(valueEditTextView.getWindowToken(), 0);
                } else {
                    InputMethodManager imm = (InputMethodManager)tmp_ctx.getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInputFromInputMethod(valueEditTextView.getWindowToken(), 0);
                }
            }});

                                                       //        valueEditTextView.setFocusable(true);
        valueEditTextView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isEditTextFocused = true;
                return false;
            }
        });
        setStatus(currentState);

//        handler.postDelayed(new AutoDecrementer(),100);
    }

//    private class AutoDecrementer implements Runnable {
//        @Override
//        public void run() {
//            if(isEditTextFocused){
//                editTextFocus();
//                handler.postDelayed(new AutoDecrementer(), 100);
//            }
//        }
//    }
//
//    void editTextFocus(){
//        valueEditTextView.requestFocus();
//    }

    void setStatus(int state){
        switch (state){
            case State.EMPTY:{
                valueEditTextView.setHint(textHint);
                editTextLabel.setText(textLabel);
                editTextErrorLabel.setVisibility(View.GONE);
                break;
            }
            case State.FOCUSED:{

                break;
            }
            case State.NOT_FOCUSED:{

                break;
            }
            default:{

                break;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return true;
//    }
}
