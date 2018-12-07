package kz.topsecurity.client.domain.introductionScreen.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import kz.topsecurity.client.domain.introductionScreen.fragment.OnboardingFragment;

public class OnboardingAdapter extends FragmentPagerAdapter {
    public OnboardingAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return OnboardingFragment.newInstance(position);
            case 1:
                return OnboardingFragment.newInstance(position);
            case 2:
                return OnboardingFragment.newInstance(position);
            default:
                return OnboardingFragment.newInstance(position);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
