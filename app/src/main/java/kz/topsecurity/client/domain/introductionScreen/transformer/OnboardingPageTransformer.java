package kz.topsecurity.client.domain.introductionScreen.transformer;

import android.support.v4.view.ViewPager;
import android.view.View;

import kz.topsecurity.client.R;

public class OnboardingPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View page, float position) {

        // Get page index from tag
        int pagePosition = (int) page.getTag();

        int pageWidth = (int) page.getWidth();
        float pageWidthTimesPosition = pageWidth * position;
        float absPosition = Math.abs(position);

        if (position <= -1.0f || position >= 1.0f) {

            // Page is not visible -- stop any running animations

        } else if (position == 0.0f) {

            // Page is selected -- reset any views if necessary

        } else {

            // Page is currently being swiped -- perform animations here

            // Fragment 1 -- Settings
//            final View tv_settings_title = page.findViewById(R.id.tv_settings_title);
//            if (tv_settings_title != null) tv_settings_title.setAlpha(1.0f - absPosition * 2);
//
//            final View tv_brightness = page.findViewById(R.id.tv_brightness);
//            if (tv_brightness != null) tv_brightness.setTranslationX(pageWidthTimesPosition * 0.5f);
//
//            final View sb_brightness = page.findViewById(R.id.sb_brightness);
//            if (sb_brightness != null) sb_brightness.setTranslationX(pageWidthTimesPosition * 0.5f);
//
//            final View tv_size = page.findViewById(R.id.tv_size);
//            if (tv_size != null) tv_size.setTranslationX(pageWidthTimesPosition * 1.2f);
//
//            final View sb_size = page.findViewById(R.id.sb_size);
//            if (sb_size != null) sb_size.setTranslationX(pageWidthTimesPosition * 1.2f);
//
//            final View tv_background = page.findViewById(R.id.tv_background);
//            if (tv_background != null) tv_background.setTranslationX(pageWidthTimesPosition * 0.9f);
//
//            final View switch_background = page.findViewById(R.id.switch_background);
//            if (switch_background != null) switch_background.setTranslationX(pageWidthTimesPosition * 0.9f);
//
//            final View cb_save = page.findViewById(R.id.cb_save);
//            if (cb_save != null) cb_save.setTranslationX(pageWidthTimesPosition * 0.3f);


            // Fragment 2 -- Social Card
//            final View tv_profile_title = page.findViewById(R.id.tv_social_card_title);
//            if (tv_profile_title != null) tv_profile_title.setAlpha(1.0f - absPosition * 2);
//
//            final ImageView profile = (ImageView) page.findViewById(R.id.iv_profile);
//            if (profile != null) profile.setTranslationX(pageWidthTimesPosition * 1.2f);
//
//            final View card = page.findViewById(R.id.ll_profile_bg);
//            if (card != null) card.setTranslationX(pageWidthTimesPosition * 0.7f);
//
//            final View button = page.findViewById(R.id.btn_follow);
//            if (button != null) button.setTranslationX(pageWidthTimesPosition * 0.2f);


            // Fragment 3 -- Feature
//            final View tv_feature_title = page.findViewById(R.id.tv_feature_title);
//            if (tv_feature_title != null) tv_feature_title.setAlpha(1.0f - absPosition * 2);
//
//            final View bike = page.findViewById(R.id.iv_bike);
//            if (bike != null) {
//                bike.setScaleX(1.0f - absPosition * 2);
//                bike.setScaleY(1.0f - absPosition * 2);
//                bike.setAlpha(1.0f - absPosition * 2);
//            }
//
//            final View bike_shadow = page.findViewById(R.id.iv_bike_shadow);
//            if (bike_shadow != null) {
//                bike_shadow.setScaleX(1.0f - absPosition * 2);
//                bike_shadow.setScaleY(1.0f - absPosition * 2);
//                bike_shadow.setAlpha(1.0f - absPosition * 2);
//            }

            // 1---------
            final View tv_security_title = page.findViewById(R.id.tv_security_title);
            if (tv_security_title != null) tv_security_title.setAlpha(1.0f - absPosition * 2);

            final View tv_security_desc = page.findViewById(R.id.tv_security_desc);
            if (tv_security_desc != null) tv_security_desc.setAlpha(1.0f - absPosition * 2);

            final View iv_security = page.findViewById(R.id.iv_security);
            if (iv_security != null) {
                iv_security.setScaleX(1.0f - absPosition * 2);
                iv_security.setScaleY(1.0f - absPosition * 2);
                iv_security.setAlpha(1.0f - absPosition * 2);
            }
//
//            final View iv_security_shadow = page.findViewById(R.id.iv_security_shadow);
//            if (iv_security_shadow != null) {
//                iv_security_shadow.setScaleX(1.0f - absPosition * 2);
//                iv_security_shadow.setScaleY(1.0f - absPosition * 2);
//                iv_security_shadow.setAlpha(1.0f - absPosition * 2);
//            }

            //2--------------
            final View tv_alert_title = page.findViewById(R.id.tv_alert_title);
            if (tv_alert_title != null) tv_alert_title.setAlpha(1.0f - absPosition * 2);


            final View tv_alert_desc = page.findViewById(R.id.tv_alert_desc);
            if (tv_alert_desc != null) tv_alert_desc.setAlpha(1.0f - absPosition * 2);

            final View iv_alert = page.findViewById(R.id.iv_alert);
            if (iv_alert != null) {
                iv_alert.setScaleX(1.0f - absPosition * 2);
                iv_alert.setScaleY(1.0f - absPosition * 2);
                iv_alert.setAlpha(1.0f - absPosition * 2);
            }

//            final View iv_alert_shadow = page.findViewById(R.id.iv_alert_shadow);
//            if (iv_alert_shadow != null) {
//                iv_alert_shadow.setScaleX(1.0f - absPosition * 2);
//                iv_alert_shadow.setScaleY(1.0f - absPosition * 2);
//                iv_alert_shadow.setAlpha(1.0f - absPosition * 2);
//            }


            //3-------------------
            final View tv_help_title = page.findViewById(R.id.tv_help_title);
            if (tv_help_title != null) tv_help_title.setAlpha(1.0f - absPosition * 2);

            final View tv_help_desc = page.findViewById(R.id.tv_help_desc);
            if (tv_help_desc != null) tv_help_desc.setAlpha(1.0f - absPosition * 2);

            final View iv_help = page.findViewById(R.id.iv_help);
            if (iv_help != null) {
                iv_help.setScaleX(1.0f - absPosition * 2);
                iv_help.setScaleY(1.0f - absPosition * 2);
                iv_help.setAlpha(1.0f - absPosition * 2);
            }

//            final View iv_help_shadow = page.findViewById(R.id.iv_help_shadow);
//            if (iv_help_shadow != null) {
//                iv_help_shadow.setScaleX(1.0f - absPosition * 2);
//                iv_help_shadow.setScaleY(1.0f - absPosition * 2);
//                iv_help_shadow.setAlpha(1.0f - absPosition * 2);
//            }
        }
    }
}
