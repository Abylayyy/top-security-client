package kz.topsecurity.client.domain.MainScreen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;

public class AlertActivity extends AppCompatActivity {

    @BindView(R.id.topConstLayout) ConstraintLayout topLayout;
    @BindView(R.id.bottomConstLayout) ConstraintLayout bottomLayout;
    @BindView(R.id.alertBtn) ImageView alertBtn;
    @BindView(R.id.profImage) RelativeLayout profImage;
    @BindView(R.id.pokupkaImage) RelativeLayout pokupkaImg;
    @BindView(R.id.callPopup) RelativeLayout callPopup;
    @BindView(R.id.skoryiPopup) RelativeLayout skoryiPopup;
    @BindView(R.id.cancelAlert) ImageView cancelAlert;
    @BindView(R.id.rectangle5) ImageView rect5;
    @BindView(R.id.rectangle1) ImageView rect1;
    @BindView(R.id.redAlertBtn) ImageView redBtn;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        ButterKnife.bind(this);

        alertBtn.setOnClickListener(v -> {
            topLayout.animate().translationY(-1000).setDuration(500).start();
            bottomLayout.animate().translationY(1000).setDuration(500).start();
            alertBtn.animate().translationY(1000).setDuration(500).start();
        });


        Animation gradient = AnimationUtils.loadAnimation(this, R.anim.transition_gradient_animation);
        gradient.setRepeatCount(1000);
        rect5.startAnimation(gradient);

        ScaleAnimation animation = new ScaleAnimation(
                0.6f, 1.2f, 0.6f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        );
        animation.setDuration(1000);
        animation.setRepeatCount(1000);
        redBtn.startAnimation(animation);

        cancelAlert.setOnClickListener(v -> {
            topLayout.animate().translationY(0).setDuration(500).start();
            bottomLayout.animate().translationY(0).setDuration(500).start();
            alertBtn.animate().translationY(0).setDuration(500).start();
        });

        profImage.setOnClickListener(v -> {
            Intent intent = new Intent(getApplication(), MainActivityNew.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finishAffinity();
        });
    }
}
