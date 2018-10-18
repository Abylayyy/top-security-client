package kz.topsecurity.client.domain.AnimationTest;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;

public class AnimationActivity extends AppCompatActivity {

    @BindView(R.id.iv_circle)
    ImageView iv_circle;
    @BindView(R.id.iv_circle2)
    ImageView iv_circle2;
    @BindView(R.id.btn_alert)
    Button btn_alert;
    @BindView(R.id.iv_dash_circle)
    ImageView iv_dash_circle;
    @BindView(R.id.iv_icon)
    ImageView iv_icon;

    AnimatorSet imageAnimatorSet;
    AnimatorSet circleAnimatorSet;
    RotateAnimation carAnimation  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        ButterKnife.bind(this);
        startAnimation();
        startCircleAnimation();
        btn_alert.setOnClickListener(v->{
            changeType();
        });
    }

    int waitingColor = R.color.colorPrimary;
    int alertColor = R.color.colorAccent;
    int troubleColor = R.color.colorSecondaryRed;

    int type = -1;

    private void changeType() {
        type++;
        stopCarAnimation();
        if (type == 4)
            type = 0;
        if (type == 0) {
            setColorToImageView(waitingColor);
            iv_icon.setImageResource(R.drawable.ic_operator);

        }
        if (type == 1) {
            setColorToImageView(alertColor);
            iv_icon.setImageResource(R.drawable.ic_mrrt_car);
        }
        if (type == 2) {
            startCarAnimation();

        }
        if (type == 3) {
            setColorToImageView(troubleColor);
            iv_icon.setImageResource(R.drawable.ic_error);
        }
    }

    private void stopCarAnimation() {
        if(carAnimation!=null)
            carAnimation.cancel();
    }


    private void startCarAnimation() {//iv_dash_circle
        int width = iv_icon.getWidth();
        int width2 = iv_dash_circle.getWidth();
//        carAnimation = new RotateAnimation(0.0f, 360.0f,
//                Animation.ABSOLUTE, x/2, Animation.ABSOLUTE,
//                y/2);
        carAnimation = new RotateAnimation(0.0f, 360.0f,
                Animation.ABSOLUTE, width/2, Animation.ABSOLUTE,
                (width+width2/2));
        carAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        carAnimation.setRepeatMode(ObjectAnimator.RESTART);
        carAnimation.setDuration(15*1000);
        iv_icon.startAnimation(carAnimation);
//        carAnimation.start();
    }

    private void setColorToImageView(int color) {
        iv_circle.setColorFilter(ContextCompat.getColor(this, color), android.graphics.PorterDuff.Mode.MULTIPLY);
        iv_circle2.setColorFilter(ContextCompat.getColor(this, color), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    ArrayList<Animator> animatorList = new ArrayList<>();

    private void startAnimation() {
        int rippleDurationTime = 1800;
        imageAnimatorSet = new AnimatorSet();
        imageAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        imageAnimatorSet.setDuration(rippleDurationTime);
        setAnimator(iv_circle,rippleDurationTime/2,0);
        setAnimator(iv_circle2,rippleDurationTime/2,rippleDurationTime/2);
        imageAnimatorSet.playTogether(animatorList);
        imageAnimatorSet.start();
    }

    private void startCircleAnimation(){
        circleAnimatorSet = new AnimatorSet();
        final ObjectAnimator alphaAnimator2= ObjectAnimator.ofFloat(iv_dash_circle, "rotation", 0f, 360f);
        alphaAnimator2.setRepeatCount(ObjectAnimator.INFINITE);
        alphaAnimator2.setRepeatMode(ObjectAnimator.RESTART);
        alphaAnimator2.setDuration(15*1000);
        circleAnimatorSet.play(alphaAnimator2);
        circleAnimatorSet.start();
    }

    private void setAnimator(ImageView imageView, int anim_time, int delay) {
        final ObjectAnimator alphaAnimator1= ObjectAnimator.ofFloat(imageView, "ScaleX", 1.0f, 6.0f);
        alphaAnimator1.setRepeatCount(ObjectAnimator.INFINITE);
        alphaAnimator1.setRepeatMode(ObjectAnimator.RESTART);
        alphaAnimator1.setStartDelay(delay);
        alphaAnimator1.setDuration(anim_time);
        animatorList.add(alphaAnimator1);
        final ObjectAnimator alphaAnimator2= ObjectAnimator.ofFloat(imageView, "ScaleY", 1.0f, 6.0f);
        alphaAnimator2.setRepeatCount(ObjectAnimator.INFINITE);
        alphaAnimator2.setRepeatMode(ObjectAnimator.RESTART);
        alphaAnimator2.setStartDelay(delay);
        alphaAnimator2.setDuration(anim_time);
        animatorList.add(alphaAnimator2);
        final ObjectAnimator alphaAnimator3= ObjectAnimator.ofFloat(imageView, "Alpha", 1.0f, 0.0f);
        alphaAnimator3.setRepeatCount(ObjectAnimator.INFINITE);
        alphaAnimator3.setRepeatMode(ObjectAnimator.RESTART);
        alphaAnimator3.setStartDelay(delay);
        alphaAnimator3.setDuration(anim_time);
        animatorList.add(alphaAnimator3);
    }
}
