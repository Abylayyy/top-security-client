package kz.topsecurity.top_signal.ui_widgets.roundCorneredEditText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import java.lang.reflect.Field;

import kz.topsecurity.top_signal.R;

public class RoundCorneredEditText extends android.support.v7.widget.AppCompatEditText {

    private final static int TYPE_TEXT_VARIATION_PASSWORD = 129;
    private final static int TYPE_NUMBER_VARIATION_PASSWORD = 18;
    private final static int DEFAULT_PADDING = 15;
    private final int DEFAULT_COLOR = Color.parseColor("#B6B6B6");
    private boolean isClearIconVisible = false;
    private int mBackgroundColor;
    private int padding, paddingLeft, paddingTop, paddingRight, paddingBottom;
    private String fontName;
    private float mStrokeWidth = 1;
    private float mCornerRadius;
    private Drawable imgCloseButton = ContextCompat.getDrawable(getContext(), android.R.drawable.ic_menu_close_clear_cancel);
    private Drawable drawableEnd;
    private boolean isPassword = false;
    private boolean isShowingPassword = false;
    private int clearIconTint;
    private int hideShowIconTint;
    private boolean isBorderView = false;
    private int mNormalColor;
    private int mFocusedColor;
    private int mErrorColor;
    private Drawable lineDrawable;
    private CustomEditTextFocusChanged customEditTextFocusChanged;

    public RoundCorneredEditText(Context context) {
        super(context);
        init(context, null);
    }

    public RoundCorneredEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundCorneredEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundCorneredEditText);
            padding = a.getDimensionPixelSize(R.styleable.RoundCorneredEditText_android_padding, -1);
            paddingLeft = a.getDimensionPixelSize(R.styleable.RoundCorneredEditText_android_paddingLeft, DEFAULT_PADDING);
            paddingTop = a.getDimensionPixelSize(R.styleable.RoundCorneredEditText_android_paddingTop, DEFAULT_PADDING);
            paddingRight = a.getDimensionPixelSize(R.styleable.RoundCorneredEditText_android_paddingRight, DEFAULT_PADDING);
            paddingBottom = a.getDimensionPixelSize(R.styleable.RoundCorneredEditText_android_paddingBottom, DEFAULT_PADDING);
            isClearIconVisible = a.getBoolean(R.styleable.RoundCorneredEditText_libIZO_setClearIconVisible, isClearIconVisible);
            isBorderView = a.getBoolean(R.styleable.RoundCorneredEditText_libIZO_setBorderView, isBorderView);
            mNormalColor = a.getColor(R.styleable.RoundCorneredEditText_libIZO_setBorderColor, DEFAULT_COLOR);
//            mNormalColor = a.getColor(R.styleable.RoundCorneredEditText_libIZO_setBorderColor, DEFAULT_COLOR);
            lineDrawable = a.getDrawable(R.styleable.RoundCorneredEditText_libIZO_setFocusedDrawable);
            mFocusedColor = a.getColor(R.styleable.RoundCorneredEditText_libIZO_setBorderColorFocused, DEFAULT_COLOR);
            mErrorColor = a.getColor(R.styleable.RoundCorneredEditText_libIZO_setBorderColorError, DEFAULT_COLOR);
            mBackgroundColor = a.getColor(R.styleable.RoundCorneredEditText_libIZO_setBackgroundColor, Color.TRANSPARENT);
            mStrokeWidth = a.getDimension(R.styleable.RoundCorneredEditText_libIZO_setStrokeWidth, mStrokeWidth);
            hideShowIconTint = a.getColor(R.styleable.RoundCorneredEditText_libIZO_hideShowPasswordIconTint, DEFAULT_COLOR);
            clearIconTint = a.getColor(R.styleable.RoundCorneredEditText_libIZO_clearIconTint, DEFAULT_COLOR);
            this.fontName = a.getString(R.styleable.RoundCorneredEditText_libIZO_setFont);
            // set corner radius
            mCornerRadius = a.getDimension(R.styleable.RoundCorneredEditText_libIZO_setCornerRadius, 1);
            if (isBorderView) {
                setBackGroundOfLayout(getShapeBackground(mNormalColor));
                setCursorColor(mNormalColor);
            } else {
                padding(false);
            }
            if (getInputType() == TYPE_TEXT_VARIATION_PASSWORD || getInputType() == TYPE_NUMBER_VARIATION_PASSWORD) {
                isPassword = true;
                this.setMaxLines(1);
            } else if (getInputType() == EditorInfo.TYPE_CLASS_PHONE) {
                this.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            }
            if (!isPassword && isClearIconVisible) {
                handleClearButton();
            }
            if (isPassword)
                if (!TextUtils.isEmpty(getText())) {
                    showPasswordVisibilityIndicator(true);
                } else {
                    showPasswordVisibilityIndicator(false);
                }
            setOnTouchListener(new OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    RoundCorneredEditText editText = RoundCorneredEditText.this;
                    if (editText.getCompoundDrawables()[2] == null)
                        return false;
                    if (event.getAction() != MotionEvent.ACTION_UP)
                        return false;
                    if (isPassword) {
                        if (event.getX() > editText.getWidth() - editText.getPaddingRight() - drawableEnd.getIntrinsicWidth()) {
                            togglePasswordVisibility();
                            event.setAction(MotionEvent.ACTION_CANCEL);
                        }
                    } else if (isClearIconVisible) {
                        if (event.getX() > editText.getWidth() - editText.getPaddingRight() - imgCloseButton.getIntrinsicWidth()) {
                            editText.setText("");
                            RoundCorneredEditText.this.handleClearButton();
                        }
                    }
                    return false;
                }
            });
            setFont();
            a.recycle();
        }
    }


    public void setCursorColor(@ColorInt int color) {
        try {
            // Get the cursor resource id
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(this);

            // Get the editor
            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(this);

            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(this.getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            // Set the drawables
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception ignored) {
        }
    }


    private void setFont() {
        if (fontName != null) {
            try {
                setTypeface(Typeface.createFromAsset(getContext().getAssets(), fontName));
            } catch (Exception ignored) {
                setTypeface(Typeface.DEFAULT);
            }
        } else {
            setTypeface(Typeface.DEFAULT);
        }
    }

    /**
     * This method is used to set the rectangle box on EditText
     */
    private void setBackGroundOfLayout(Drawable shape) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(shape);
        } else {
            setBackgroundDrawable(shape);
        }
        padding(true);
    }

    private void padding(boolean isRound) {
        int extraPadding;
        int extrapad;
        if (isRound) {
            extraPadding = 5;
            extrapad = 0;
        } else {
            extrapad = 5;
            extraPadding = 0;
        }
        if (padding != -1) {
            super.setPadding(padding + extraPadding, padding, padding, padding + extrapad);
        } else {
            super.setPadding(paddingLeft + extraPadding, paddingTop, paddingRight, paddingBottom + extrapad);
        }
    }


    /**
     * This method is used to draw the rectangle border view with color
     */
    @SuppressLint("WrongConstant")
    private Drawable getShapeBackground(@ColorInt int color) {

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(mCornerRadius);
        shape.setColor(mBackgroundColor);
        shape.setStroke((int) mStrokeWidth, color);
        return shape;
    }

    @SuppressLint("WrongConstant")
    private Drawable getFocusedShapeBackground(@ColorInt int color) {
        return lineDrawable;
    }

    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    private void handleClearButton() {
        if (isClearIconVisible) {
            DrawableCompat.setTint(imgCloseButton, clearIconTint);
            imgCloseButton.setBounds(0, 0, 43, 43);
            if (this.getText().length() == 0) {
                this.setCompoundDrawables(this.getCompoundDrawables()[0], this.getCompoundDrawables()[1], null, this.getCompoundDrawables()[3]);
            } else {
                this.setCompoundDrawables(this.getCompoundDrawables()[0], this.getCompoundDrawables()[1], imgCloseButton, this.getCompoundDrawables()[3]);
            }
        }
    }


    @Override
    public void onTextChanged(CharSequence s, int i, int i1, int i2) {
        try {
            if (isPassword) {
                if (s.length() > 0) {
                    showPasswordVisibilityIndicator(true);
                } else {
                    isShowingPassword = false;
                    maskPassword();
                    showPasswordVisibilityIndicator(false);
                }
            } else if (isClearIconVisible)
                RoundCorneredEditText.this.handleClearButton();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPasswordVisibilityIndicator(boolean show) {
        if (show) {
            Drawable original = isShowingPassword ?
                    ContextCompat.getDrawable(getContext(), R.drawable.ic_closed_eye_normal) :
                    ContextCompat.getDrawable(getContext(), R.drawable.ic_open_eye_normal);
            original.mutate();
            DrawableCompat.setTint(original, hideShowIconTint);
            original.setBounds(0, 0, 43, 43);
            drawableEnd = original;
            this.setCompoundDrawables(this.getCompoundDrawables()[0], this.getCompoundDrawables()[1], original, this.getCompoundDrawables()[3]);
        } else {
            this.setCompoundDrawables(this.getCompoundDrawables()[0], this.getCompoundDrawables()[1], null, this.getCompoundDrawables()[3]);
        }
    }

    //make it visible
    private void unmaskPassword() {
        setTransformationMethod(null);
    }

    //hide it
    private void maskPassword() {
        setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    public void togglePasswordVisibility() {
        // Store the selection
        int selectionStart = this.getSelectionStart();
        int selectionEnd = this.getSelectionEnd();
        // Set transformation method to show/hide password
        if (isShowingPassword) {
            maskPassword();
        } else {
            unmaskPassword();
        }
        // Restore selection
        this.setSelection(selectionStart, selectionEnd);
        // Toggle flag and show indicator
        isShowingPassword = !isShowingPassword;
        showPasswordVisibilityIndicator(true);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        isError = false;
        if(focused){
            setBackGroundOfLayout(getFocusedShapeBackground(mFocusedColor));
        }
        else{
            setBackGroundOfLayout(getShapeBackground(mNormalColor));
        }
        if(customEditTextFocusChanged!=null)
            customEditTextFocusChanged.onFocusChanged();
    }

    public void addOnFocusChanged(CustomEditTextFocusChanged customEditTextFocusChanged){
        this.customEditTextFocusChanged = customEditTextFocusChanged;
    }

    private boolean isError = false;

    public void setError(){
        isError = true;
        setBackGroundOfLayout(getShapeBackground(mErrorColor));
    }

    public boolean isErrorState(){
        return isError;
    }


    public void removeError() {
        isError = false;
        if(isFocused()){
            setBackGroundOfLayout(getFocusedShapeBackground(mFocusedColor));
        }
        else{
            setBackGroundOfLayout(getShapeBackground(mNormalColor));
        }
    }
}

