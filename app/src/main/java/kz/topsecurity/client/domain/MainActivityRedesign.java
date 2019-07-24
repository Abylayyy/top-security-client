package kz.topsecurity.client.domain;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;

public class MainActivityRedesign extends AppCompatActivity  {
    float dX = 0;
    float dY = 0;
    boolean mIsDown = false;

    AnimationDrawable animation;

    @BindView(R.id.alert_button_redesign)
    ImageButton alert_button_redesign;

    @BindView(R.id.top_layout)
    RelativeLayout top_layout;

    @BindView(R.id.bottom_layout)
    RelativeLayout bottom_layout;

    @BindView(R.id.rectangle5)
    ImageView rectangle5;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_redesign);

        ButterKnife.bind(this);

        Animation transition_top_layout = AnimationUtils.loadAnimation(this,R.anim.transition_top_layout);
        Animation transition_bottom_layout = AnimationUtils.loadAnimation(this,R.anim.transition_bottom_layout);
        Animation transition_gradient_animation = AnimationUtils.loadAnimation(this,R.anim.transition_gradient_animation);
        transition_gradient_animation.setRepeatCount(Animation.INFINITE);
        transition_gradient_animation.setRepeatMode(Animation.REVERSE);
        alert_button_redesign.setOnClickListener(v -> {
            top_layout.startAnimation(transition_top_layout);
            bottom_layout.startAnimation(transition_bottom_layout);
            alert_button_redesign.startAnimation(transition_bottom_layout);
        });
        rectangle5.startAnimation(transition_gradient_animation);
    }
}