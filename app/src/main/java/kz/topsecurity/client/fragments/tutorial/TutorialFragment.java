package kz.topsecurity.client.fragments.tutorial;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import kz.topsecurity.client.R;
import kz.topsecurity.client.helper.SharedPreferencesManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TutorialFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TutorialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorialFragment extends Fragment {
    private static final String ARG_PARAM1 = "type";
    ArrayList<String> shownTutsList ;
    private int mParam1;

    private OnFragmentInteractionListener mListener;
    private Unbinder unbinder;
    @BindView(R.id.iv_tuts) ImageView iv_tuts;
    @BindView(R.id.tv_tuts_title) TextView tv_tuts_title;
    @BindView(R.id.tv_tuts_desc) TextView tv_tuts_desc;
    @BindView(R.id.btn_ok) Button btn_ok;
    @BindView(R.id.cl_main_container) ConstraintLayout cl_main_container;

    public static final int MAIN_ACTIVITY = 11;
    public static final int PLACES_ACTIVITY = 12;
    public static final int CONTACTS_ACTIVITY = 13;
    public static final int SETTINGS_ACTIVITY = 14;

    boolean setTutsLogic(int type){
        String[] prepare_pages_pack ;
        switch (type){
            case MAIN_ACTIVITY:{
                prepare_pages_pack = new String[2];
                prepare_pages_pack[0] = ALERT_PAGE;
                prepare_pages_pack[1] = CANCEL_ALERT_PAGE;
                break;
            }
            case PLACES_ACTIVITY:{
                prepare_pages_pack = new String[1];
                prepare_pages_pack[0] = PLACES_PAGE;
                break;
            }
            case CONTACTS_ACTIVITY:{
                prepare_pages_pack = new String[1];
                prepare_pages_pack[0] = CONTACTS_PAGE;
                break;
            }
            case SETTINGS_ACTIVITY:{
                prepare_pages_pack = new String[2];
                prepare_pages_pack[0] = SETTINGS_PAGE;
                prepare_pages_pack[1] = VOLUME_BUTTON_PAGE;
                break;
            }
            default:{
                finishFragment();
                return false;
            }
        }

        List<String> setValuesToPack = new ArrayList<>();
        shownTutsList = SharedPreferencesManager.getShownTutsList(getContext());
        for (String current_page :
                prepare_pages_pack) {
            if(!shownTutsList.contains(current_page)){
                setValuesToPack.add(current_page);
            }
        }
        if(setValuesToPack.isEmpty()){
            checkTutsStatus();
            finishFragment();
            return false;
        }
        else{
            current_pages_pack = new String[setValuesToPack.size()];
            for(int i=0; i<setValuesToPack.size(); i++){
                current_pages_pack[i] = setValuesToPack.get(i);
            }
        }
        return true;
    }

    private static final String ALERT_PAGE = "ALERT_PAGE";
    private static final String CANCEL_ALERT_PAGE = "CANCEL_ALERT_PAGE";
    private static final String PLACES_PAGE = "PLACES_PAGE";
    private static final String CONTACTS_PAGE = "CONTACTS_PAGE";
    private static final String SETTINGS_PAGE = "SETTINGS_PAGE";
    private static final String VOLUME_BUTTON_PAGE = "VOLUME_BUTTON_PAGE";

    String[] current_pages_pack;
    int currentPage = -1;
    boolean isAnimationActive = false;

    public TutorialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment TutorialFragment.
     */
    public static TutorialFragment newInstance(int param1) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1,-1);
        }
    }

    private void setupPage() {
        if(iv_tuts==null || tv_tuts_desc==null || tv_tuts_title == null)
            return;
        setDataToViews();
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animateElements();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        cl_main_container.startAnimation(animation);
        isAnimationActive = true;

    }

    void setDataToViews(){
        int imageRes = -1;
        int titleText = -1;
        int descText = -1;

        String pageIndex = current_pages_pack[currentPage];
        switch(pageIndex){
            case ALERT_PAGE:{
                titleText = R.string.tuts_alert_title;
                descText = R.string.tuts_alert_desc;
                imageRes = R.drawable.tuts_main_page_alert_btn;
                break;
            }
            case CANCEL_ALERT_PAGE:{
                titleText = R.string.tuts_cancel_alert_title;
                descText = R.string.tuts_cancel_alert_desc;
                imageRes = R.drawable.tuts_main_page_alert_cancel_btn;
                break;
            }
            case SETTINGS_PAGE :{
                titleText = R.string.tuts_settings_title;
                descText = R.string.tuts_settings_desc;
                imageRes = R.drawable.tuts_settings_page;
                break;
            }
            case  PLACES_PAGE:{
                titleText = R.string.tuts_places_title;
                descText = R.string.tuts_places_desc;
                imageRes = R.drawable.tuts_place_page_map_view;
                break;
            }
            case  CONTACTS_PAGE:{
                titleText = R.string.tuts_contacts_title;
                descText = R.string.tuts_contacts_desc;
                imageRes = R.drawable.tuts_contacts_page_phone;
                break;
            }
            case VOLUME_BUTTON_PAGE:{
                titleText = R.string.tuts_volume_btn_to_alert_title;
                descText = R.string.tuts_volume_btn_to_alert_desc;
                imageRes = R.drawable.tuts_settings_volume_btn;
                break;
            }
        }
        iv_tuts.setImageResource(imageRes);
        tv_tuts_title.setText(titleText);
        tv_tuts_desc.setText(descText);
    }

    Runnable afterHideAnimation ;

    void nextPage(){
        if(isAnimationActive)
            return;
        if(currentPage < current_pages_pack.length)
        {
            if(shownTutsList==null)
                shownTutsList = new ArrayList<>();
            shownTutsList.add(current_pages_pack[currentPage]);
        }
        currentPage++;
        if(currentPage >= current_pages_pack.length){
            afterHideAnimation = new Runnable() {
                @Override
                public void run() {
                    checkTutsStatus();
                    finishFragment();
                }
            };
        }
        else{
            afterHideAnimation = new Runnable() {
                @Override
                public void run() {
                    setDataToViews();
                    animateElements();
                }
            };
        }
        hideCurrentPage();
        return ;
    }

    void checkTutsStatus(){
        SharedPreferencesManager.setShownTutsList(getContext(),shownTutsList);
        if(shownTutsList.size() >= 6){
            SharedPreferencesManager.setIsTutsShown(getContext(), true);
        }
    }

    private void finishFragment() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_fast);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(mListener!=null)
                    mListener.onOkButtonClick();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        cl_main_container.startAnimation(animation);
    }

    private void hideCurrentPage() {
        btn_ok.setEnabled(false);
        Animation fade_out = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        fade_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_tuts_title.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation fade_out_fast = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_fast);
        fade_out_fast.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv_tuts.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation fade_out2 = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        fade_out2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_tuts_desc.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation fade_out3 = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        fade_out3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btn_ok.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        tv_tuts_title.startAnimation(fade_out);
        iv_tuts.startAnimation(fade_out_fast);
        tv_tuts_desc.startAnimation(fade_out2);
        btn_ok.startAnimation(fade_out3);
        new Handler().postDelayed(afterHideAnimation,1500);
    }

    private void animateElements() {
        stopAnimations();
        btn_ok.setEnabled(true);
        Animation scaleFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.scale_fade_anim);
        scaleFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animateTexts();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv_tuts.setVisibility(View.VISIBLE);
        iv_tuts.startAnimation(scaleFadeIn);

    }

    private void animateTexts() {
        Animation fade_in = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fade_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showButton();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        tv_tuts_title.setVisibility(View.VISIBLE);
        tv_tuts_desc.setVisibility(View.VISIBLE);
        tv_tuts_title.startAnimation(fade_in);
        tv_tuts_desc.startAnimation(fade_in);

    }

    Handler handler;
    Runnable r ;
    private static final int delayMillis  = 1000;

    private void showButton() {
        if(handler!=null && r!=null)
            handler.removeCallbacks(r);
        handler = new Handler();
        r = () -> {
            btn_ok.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isAnimationActive = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            btn_ok.startAnimation(animation);
        };

        handler.postDelayed(r, delayMillis);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tutorial, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(SharedPreferencesManager.getIsTutsShown(getContext()))
        {
            finishFragment();
            return;
        }
        if(mParam1!=-1 && setTutsLogic(mParam1) ){
            currentPage = 0;
            setupPage();
        }
    }

    @OnClick(R.id.btn_ok)
    public void onOkButtonPressed() {
        nextPage();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onOkButtonClick();
    }

    void stopAnimations(){
        if(handler!=null && r!=null)
            handler.removeCallbacks(r);
        iv_tuts.clearAnimation();
        tv_tuts_desc.clearAnimation();
        tv_tuts_title.clearAnimation();
        btn_ok.clearAnimation();
        isAnimationActive = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopAnimations();
        unbinder.unbind();
    }
}
