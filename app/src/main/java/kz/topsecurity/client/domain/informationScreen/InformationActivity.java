package kz.topsecurity.client.domain.informationScreen;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.FeedbackScreen.FeedbackActivity;
import kz.topsecurity.client.domain.informationScreen.aboutScreen.AboutActivity;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.DateUtils;

public class InformationActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_about_app)
    TextView tv_about_app;
    @BindView(R.id.tv_user_instruction)
    TextView tv_user_instruction;
    @BindView(R.id.tv_feedback)
    TextView tv_feedback;
    @BindView(R.id.tv_user_agreement)
    TextView tv_user_agreement;
    @BindView(R.id.tv_app_version)
    TextView tv_app_version;
    @BindView(R.id.tv_update_date)
    TextView tv_update_date;
    @BindView(R.id.iv_app_icon)
    ImageView iv_app_icon;

    Handler handler;
    Runnable runnable ;
    int clickRate = 0;
    private static final int MAX_CLICK_RATE = 10;
    boolean clickCounterActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_activity_information);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tv_about_app.setOnClickListener(this);
        tv_user_instruction.setOnClickListener(this);
        tv_feedback.setOnClickListener(this);
        tv_user_agreement.setOnClickListener(this);
        setAppInfo();
        iv_app_icon.setOnClickListener(v->{
            if(!clickCounterActive) {
                clickRate = 0;
                clickCounterActive = true;
            }
            clickRate++;
            if(clickRate == MAX_CLICK_RATE){
                startAnimation();
            }
            if(handler==null) {
                handler = new Handler();
            }
            if(runnable!=null)
                handler.removeCallbacks(runnable);
            runnable = new Runnable() {
                @Override
                public void run() {
                    clickCounterActive = false;
                }
            };
            handler.postDelayed(runnable,800);
        });
    }

    int anim_time = 600;
    boolean isAnimationActive = false;
    private void startAnimation() {
        if(isAnimationActive)
            return;

        ArrayList<Animator> animatorList = new ArrayList<>();
        final ObjectAnimator alphaAnimator1= ObjectAnimator.ofFloat(iv_app_icon, "ScaleX", 1.0f, 0.8f);
        alphaAnimator1.setRepeatCount(1);
        alphaAnimator1.setRepeatMode(ObjectAnimator.REVERSE);
        alphaAnimator1.setDuration(anim_time/2);
        animatorList.add(alphaAnimator1);
        final ObjectAnimator alphaAnimator2= ObjectAnimator.ofFloat(iv_app_icon, "ScaleY", 1.0f, 0.8f);
        alphaAnimator2.setRepeatCount(1);
        alphaAnimator2.setRepeatMode(ObjectAnimator.REVERSE);
        alphaAnimator2.setDuration(anim_time/2);
        animatorList.add(alphaAnimator2);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setDuration(anim_time);
        animatorSet.playTogether(animatorList);
        animatorSet.start();
        isAnimationActive = true;
        Handler animHandler = new Handler();
        animHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isAnimationActive = false;
            }
        },anim_time+200);
    }

    private void setAppInfo() {
        tv_app_version.setText(String.format("Версия: %s",getAppVersion()));
        tv_update_date.setText(String.format("Дата обновления: %s",getAppUpdateDate()));

    }

    private String getAppUpdateDate() {
        PackageManager pm = getPackageManager();
        ApplicationInfo appInfo = null;
        try {
            appInfo = pm.getApplicationInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(appInfo==null)
            return "";
        String appFile = appInfo.sourceDir;
        long installed = new File(appFile).lastModified();
        return DateUtils.convertTimeStampToReadableDate(String.valueOf(installed),DateUtils.FULL_DATE_WITHOUT_TIME);
    }

    private String getAppVersion() {
        PackageManager pm = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(packageInfo==null)
            return "";
        return packageInfo.versionName;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_about_app:{
                Intent intent = new Intent(this,AboutActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.tv_feedback:{
                Intent intent = new Intent(this,FeedbackActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.tv_user_instruction:{
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.INSTRUCTION_LINK));
                startActivity(i);
                break;
            }
            case R.id.tv_user_agreement:{
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.PRIVACY_POLICY_LINK));
                startActivity(i);
                break;
            }
        }
    }
}