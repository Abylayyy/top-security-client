package kz.topsecurity.client.ui_widgets;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;

import kz.topsecurity.client.R;

public class CustomRippleBackground extends RelativeLayout {

    private static final int DEFAULT_RIPPLE_COUNT=6;
    private static final int DEFAULT_DURATION_TIME=3000;
    private static final float DEFAULT_SCALE=6.0f;
    private static final int DEFAULT_FILL_TYPE=0;

    private int rippleColor;
    private int rippleColor2;
    private float rippleStrokeWidth;
    private float rippleRadius;
    private int rippleDurationTime;
    private int rippleAmount;
    private int rippleDelay;
    private float rippleScale;
    private int rippleType;
    private Paint paint;
    private boolean animationRunning=false;
    private AnimatorSet animatorSet;
    private ArrayList<Animator> animatorList;
    private LayoutParams rippleParams;
    private ArrayList<CustomRippleBackground.RippleView> rippleViewList=new ArrayList<CustomRippleBackground.RippleView>();
    private Paint white_paint;

    public void setRippleColor(int rippleColor) {
     //   this.rippleColor = rippleColor;
       // initPaint();
    }

    public CustomRippleBackground(Context context) {
        super(context);
    }

    public CustomRippleBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomRippleBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {
        if (isInEditMode())
            return;

        if (null == attrs) {
            throw new IllegalArgumentException("Attributes should be provided to this view,");
        }

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, com.skyfishjy.library.R.styleable.RippleBackground);
        rippleColor=typedArray.getColor(com.skyfishjy.library.R.styleable.RippleBackground_rb_color, getResources().getColor(com.skyfishjy.library.R.color.rippelColor));
        rippleStrokeWidth=typedArray.getDimension(com.skyfishjy.library.R.styleable.RippleBackground_rb_strokeWidth, getResources().getDimension(com.skyfishjy.library.R.dimen.rippleStrokeWidth));
        rippleRadius=typedArray.getDimension(com.skyfishjy.library.R.styleable.RippleBackground_rb_radius,getResources().getDimension(com.skyfishjy.library.R.dimen.rippleRadius));
        rippleDurationTime=typedArray.getInt(com.skyfishjy.library.R.styleable.RippleBackground_rb_duration,DEFAULT_DURATION_TIME);
        rippleAmount=typedArray.getInt(com.skyfishjy.library.R.styleable.RippleBackground_rb_rippleAmount,DEFAULT_RIPPLE_COUNT);
        rippleScale=typedArray.getFloat(com.skyfishjy.library.R.styleable.RippleBackground_rb_scale,DEFAULT_SCALE*4);
        rippleType=typedArray.getInt(com.skyfishjy.library.R.styleable.RippleBackground_rb_type,DEFAULT_FILL_TYPE);
        typedArray.recycle();

        rippleDelay=rippleDurationTime/rippleAmount;

        initPaint();

        rippleParams=new LayoutParams((int)(2*(rippleRadius+rippleStrokeWidth)),(int)(2*(rippleRadius+rippleStrokeWidth)));
        rippleParams.addRule(CENTER_IN_PARENT, TRUE);

        animatorSet = new AnimatorSet();
        animatorSet.setDuration(rippleDurationTime);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorList=new ArrayList<Animator>();

        for(int i=0;i<rippleAmount;i++){
            CustomRippleBackground.RippleView rippleView=new CustomRippleBackground.RippleView(getContext());
            addView(rippleView,rippleParams);
            rippleViewList.add(rippleView);
            final ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1.0f, rippleScale);
            scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            scaleXAnimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleXAnimator.setStartDelay(i*rippleDelay);
            animatorList.add(scaleXAnimator);
            final ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1.0f, rippleScale);
            scaleYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            scaleYAnimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleYAnimator.setStartDelay(i*rippleDelay);
            animatorList.add(scaleYAnimator);
            final ObjectAnimator alphaAnimator= ObjectAnimator.ofFloat(rippleView, "Alpha", 1.0f, 1.0f);
            alphaAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            alphaAnimator.setRepeatMode(ObjectAnimator.RESTART);
            alphaAnimator.setStartDelay(i * rippleDelay);
            animatorList.add(alphaAnimator);
        }

        animatorSet.playTogether(animatorList);
    }

    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        if(rippleType==DEFAULT_FILL_TYPE){
            rippleStrokeWidth=0;
            paint.setStyle(Paint.Style.FILL);
        }else
            paint.setStyle(Paint.Style.STROKE);
        paint.setColor(rippleColor);

        white_paint = new Paint();
        white_paint.setAntiAlias(true);
        if(rippleType==DEFAULT_FILL_TYPE){
            rippleStrokeWidth=0;
            white_paint.setStyle(Paint.Style.FILL);
        }else
            white_paint.setStyle(Paint.Style.STROKE);
        white_paint.setColor(getResources().getColor(R.color.white));
    }

    private class RippleView extends View {

        public RippleView(Context context) {
            super(context);
            this.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int radius=(Math.min(getWidth(),getHeight()))/2;
            canvas.drawCircle(radius,radius,radius-rippleStrokeWidth,paint);
            canvas.drawCircle(radius,radius,radius/2,white_paint);
            canvas.drawCircle(radius,radius,radius/8,paint);
            canvas.drawCircle(radius,radius,radius/10,white_paint);
        }
    }

    public void startRippleAnimation(){
        if(!isRippleAnimationRunning()){
            for(CustomRippleBackground.RippleView rippleView:rippleViewList){
                rippleView.setVisibility(VISIBLE);
            }
            animatorSet.start();
            animationRunning=true;
        }
    }

    public void stopRippleAnimation(){
        if(isRippleAnimationRunning()){
            animatorSet.end();
            animationRunning=false;
        }
    }

    public boolean isRippleAnimationRunning(){
        return animationRunning;
    }
}
