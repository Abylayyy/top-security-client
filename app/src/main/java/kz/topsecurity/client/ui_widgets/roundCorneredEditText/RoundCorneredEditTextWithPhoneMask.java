package kz.topsecurity.client.ui_widgets.roundCorneredEditText;

import android.content.Context;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.Log;

import com.redmadrobot.inputmask.MaskedTextChangedListener;

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
                (maskFilled, extractedValue) -> {
                    rawText = extractedValue;
                    Log.d("TAG", extractedValue);
                    Log.d("TAG", String.valueOf(maskFilled));
                }
        );

        setHint(listener.placeholder());
    }

    public String getRawText(){
        return rawText;
    }
}
