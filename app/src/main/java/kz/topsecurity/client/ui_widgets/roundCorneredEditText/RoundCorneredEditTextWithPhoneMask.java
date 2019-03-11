package kz.topsecurity.client.ui_widgets.roundCorneredEditText;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.redmadrobot.inputmask.MaskedTextChangedListener;

import kz.topsecurity.client.R;

public class RoundCorneredEditTextWithPhoneMask  extends RoundCorneredEditText   {

    private static final String TAG = RoundCorneredEditTextWithMask.class.getSimpleName();
    private String rawText = "";
    public RoundCorneredEditTextWithPhoneMask(Context context) {
        super(context);
        init();
    }

    public RoundCorneredEditTextWithPhoneMask(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundCorneredEditTextWithPhoneMask(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private boolean hasHint() {
        return getHint() != null;
    }

    private void init() {
        setInputType(InputType.TYPE_CLASS_NUMBER);
        setKeyListener(DigitsKeyListener.getInstance("1234567890+-() "));
        final MaskedTextChangedListener listener = MaskedTextChangedListener.Companion.installOn(
                this,
                "+7([000])[000]-[00]-[00]",
                new MaskedTextChangedListener.ValueListener() {
                    @Override
                    public void onTextChanged(boolean maskFilled, @NonNull final String extractedValue) {
                        rawText = extractedValue;
                        Log.d("TAG", extractedValue);
                        Log.d("TAG", String.valueOf(maskFilled));
                    }
                }
        );

        setHint(listener.placeholder());
    }

    public String getRawText(){
        return rawText;
    }
}
