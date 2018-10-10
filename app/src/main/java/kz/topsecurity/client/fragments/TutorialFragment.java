package kz.topsecurity.client.fragments;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import kz.topsecurity.client.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TutorialFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TutorialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorialFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "type";

    // TODO: Rename and change types of parameters
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

    void setTutsLogic(int type){
        switch (type){
            case MAIN_ACTIVITY:{
                current_pages_pack = new int[2];
                current_pages_pack[0] = ALERT_PAGE;
                current_pages_pack[1] = CANCEL_ALERT_PAGE;
                break;
            }
            case PLACES_ACTIVITY:{
                current_pages_pack = new int[1];
                current_pages_pack[0] = PLACES_PAGE;
                break;
            }
            case CONTACTS_ACTIVITY:{
                current_pages_pack = new int[1];
                current_pages_pack[0] = CONTACTS_PAGE;
                break;
            }
            case SETTINGS_ACTIVITY:{
                current_pages_pack = new int[2];
                current_pages_pack[0] = SETTINGS_PAGE;
                current_pages_pack[1] = VOLUME_BUTTON_PAGE;
                break;
            }
            default:{

                break;
            }
        }
    }

    private static final int ALERT_PAGE = 937;
    private static final int CANCEL_ALERT_PAGE = 901;
    private static final int PLACES_PAGE = 715;
    private static final int CONTACTS_PAGE = 285;
    private static final int SETTINGS_PAGE = 145;
    private static final int VOLUME_BUTTON_PAGE = 527;

    int[] current_pages_pack;
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
    // TODO: Rename and change types and number of parameters
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

        switch (mParam1){
            case MAIN_ACTIVITY:{
                iv_tuts.setImageResource(R.drawable.tuts_main_page_alert_btn);
                iv_tuts.setOnClickListener(r->{
                    btn_ok.setVisibility(View.INVISIBLE);
                    iv_tuts.setVisibility(View.INVISIBLE);
                    tv_tuts_title.setVisibility(View.INVISIBLE);
                    tv_tuts_desc.setVisibility(View.INVISIBLE);
                    currentPage = 0;
                    setupPage();
                });
                break;
            }
        }
    }

    void setDataToViews(){
        int imageRes = -1;
        int titleText = -1;
        int descText = -1;

        int pageIndex = current_pages_pack[currentPage];
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
        currentPage++;
        if(currentPage >= current_pages_pack.length){
            afterHideAnimation = new Runnable() {
                @Override
                public void run() {
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

        if(mParam1!=-1){
            setTutsLogic(mParam1);
            currentPage = 0;
            setupPage();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
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
        // TODO: Update argument type and name
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
