package kz.topsecurity.client.domain.introductionScreen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kz.topsecurity.client.R;

public class OnboardingFragment extends Fragment {

    private static final String BACKGROUND_COLOR = "background_color";
    private static final String PAGE = "page";

    private int backgroundColor;
    private int page;

    public static OnboardingFragment newInstance(int page) {
        OnboardingFragment fragment = new OnboardingFragment();

        Bundle b = new Bundle();
        b.putInt(PAGE, page);

        fragment.setArguments(b);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        backgroundColor = getArguments().getInt(BACKGROUND_COLOR);
        page = getArguments().getInt(PAGE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Select a layout according to the current page
        int layoutResId;
        switch (page) {
            case 0:
                layoutResId = R.layout.intro_fragment_1;
                break;
            case 1:
                layoutResId = R.layout.intro_fragment_2;
                break;
            case 2:
                layoutResId = R.layout.intro_fragment_3;
                break;
            default:
                layoutResId = R.layout.intro_fragment_1;
                break;
        }

        // Inflate layout resource
        View view = getActivity().getLayoutInflater().inflate(layoutResId, container, false);

        // Set the current page index as the View's tag (used for PageTransformer)
        view.setTag(page);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the background color of the root view to the color specified in newInstance()
        View background = view.findViewById(R.id.onboarding_fragment_bg);
        //background.setBackgroundColor(backgroundColor);
    }


}