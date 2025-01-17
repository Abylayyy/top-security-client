package kz.topsecurity.client.domain.PlaceScreen;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.PlaceScreen.adapter.PlaceListAdapter;
import kz.topsecurity.client.domain.PlaceScreen.adapter.PlaceListDecorator;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.fragments.tutorial.TutorialFragment;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.helper.MapHelper;
import kz.topsecurity.client.model.place.Place;
import kz.topsecurity.client.presenter.placesPresenter.PlacesPresenter;
import kz.topsecurity.client.presenter.placesPresenter.PlacesPresenterImpl;
import kz.topsecurity.client.ui_widgets.customDialog.CustomSimpleDialog;
import kz.topsecurity.client.utils.GlideApp;
import kz.topsecurity.client.view.placesView.PlacesView;

public class PlaceActivity
        extends BaseActivity<PlacesView,
        PlacesPresenter,
        PlacesPresenterImpl>
        implements OnMapReadyCallback , PlacesView , kz.topsecurity.client.domain.PlaceScreen.adapter.PlaceListAdapter.PlaceListAdapterListener {

    private GoogleMap mMap;
    private LatLng myLocation;
    private LatLng markerLocation;
    private Marker mPlaceMarker;
    private int mRadius = 15;
    boolean isMarkerSet =false;

    @BindView(R.id.sb_radius)   SeekBar sb_radius;
    @BindView(R.id.tv_radius) TextView tv_radius;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.ll_place_text_values_input_view) LinearLayout ll_place_text_values_input_view;
    @BindView(R.id.ll_place_list_view) LinearLayout ll_place_list_view;
    @BindView(R.id.ll_radius_picker) LinearLayout ll_radius_picker;
    @BindView(R.id.rv_places) RecyclerView rv_places;
    @BindView(R.id.ed_place_name) EditText ed_place_name;
    @BindView(R.id.ed_place_description) EditText ed_place_description;

    int currentViewState = UNKNOWN_VIEW;
    private static final int UNKNOWN_VIEW = -1;
    private static final int RADIUS_VIEW = 364;
    private static final int TEXT_INFO_VIEW = 123;
    private static final int LIST_VIEW = 432;

    PlaceListAdapter mPlaceListAdapter = new PlaceListAdapter(new ArrayList<>(),this);
    private RecyclerView.LayoutManager mLayoutManager;
    BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        ButterKnife.bind(this);
        initPresenter(new PlacesPresenterImpl(this));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Места");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        sb_radius.setEnabled(false);
        setRadiusViewValues(mRadius);
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setupRV();
        checkTutsStatus(savedInstanceState);
    }

    private void checkTutsStatus(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showTutorials(TutorialFragment.PLACES_ACTIVITY);
            }
        },100);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            bottomSheetBehavior.setPeekHeight(500);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            bottomSheetBehavior.setPeekHeight(500);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void setupRV() {
        rv_places.setHasFixedSize(true);
        rv_places.addItemDecoration(new PlaceListDecorator(this));
        mLayoutManager = new LinearLayoutManager(this);
        ((SimpleItemAnimator) rv_places.getItemAnimator()).setSupportsChangeAnimations(false);
        rv_places.setLayoutManager(mLayoutManager);
        mPlaceListAdapter.setMargin(this);
        rv_places.setAdapter(mPlaceListAdapter);
    }

    private final View.OnClickListener onRadiusViewButtonClick = v -> {
        if (checkRadiusData()) {
            setPlaceTextValuesView();
        }
    };

    void setPlaceRadiusView(){
        clearMapElements();
        bottomSheetBehavior.setPeekHeight(500);
        findViewById(R.id.map).setVisibility(View.VISIBLE);
        ll_place_text_values_input_view.setVisibility(View.GONE);
        ll_place_list_view.setVisibility(View.GONE);
        currentViewState = RADIUS_VIEW;
        ll_radius_picker.setVisibility(View.VISIBLE);
        sb_radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mRadius = i + 1;
                drawCircle(mRadius);
                setTextRadius(mRadius);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sb_radius.setProgress(mRadius);
        configureFabButton(R.drawable.ic_arrow_forward ,onRadiusViewButtonClick );
    }

    boolean checkRadiusData(){
        if(markerLocation!=null && mPlaceMarker!=null)
            return true;
        showToast(R.string.choose_place);
        return false;
    }

    private final View.OnClickListener placeListViewButtonClick = v -> {
        setPlaceRadiusView();
    };

    void setPlaceListView(){
        clearMapElements();
        bottomSheetBehavior.setPeekHeight(500);
        findViewById(R.id.map).setVisibility(View.VISIBLE);
        ll_place_text_values_input_view.setVisibility(View.GONE);
        ll_radius_picker.setVisibility(View.GONE);
        currentViewState = LIST_VIEW;
        ll_place_list_view.setVisibility(View.VISIBLE);
        configureFabButton(R.drawable.ic_add ,placeListViewButtonClick );
    }

    int edit_place_id = -1;
    private final View.OnClickListener textValuesViewButtonClick = v -> {
        if (checkTextValues()) {
            hideSoftKeyboard(fab);
            if(edit_place_id==-1)
                presenter.savePlace(ed_place_name.getText().toString(), markerLocation,ed_place_description.getText().toString(), mRadius);
            else
                presenter.editPlace(edit_place_id, ed_place_name.getText().toString(), markerLocation,ed_place_description.getText().toString(), mRadius);
        }
    };

    RequestOptions transforms = new RequestOptions().transforms(new CenterInside(), new RoundedCorners(10));

    void setPlaceTextValuesView(){
        bottomSheetBehavior.setPeekHeight(0);
        ll_place_list_view.setVisibility(View.GONE);
        ll_radius_picker.setVisibility(View.GONE);
        currentViewState = TEXT_INFO_VIEW;
        ll_place_text_values_input_view.setVisibility(View.VISIBLE);
        findViewById(R.id.map).setVisibility(View.GONE);
        GlideApp.with(this)
                .load("https://maps.googleapis.com/maps/api/staticmap" +
                        "?center="+ markerLocation.latitude +","+ markerLocation.longitude +
                        "&zoom=16" +
                        "&size=600x300" +
                        "&maptype=roadmap" +
                        "&markers=color:blue%7Tlabel:S%7C"+markerLocation.latitude+","+markerLocation.longitude+
                        "&key="+Constants.getGoogleMapKey())
                .apply(transforms)
                .placeholder(R.drawable.placeholder_map)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into((ImageView)findViewById(R.id.iv_map));
        configureFabButton(R.drawable.ic_done, textValuesViewButtonClick);
    }

    private void configureFabButton(int fab_icon, View.OnClickListener fab_listener){
        fab.setImageResource(fab_icon);
        fab.setOnClickListener(fab_listener);
    }

    private void clearMapElements(){
        if(mPlaceMarker!=null) mPlaceMarker.remove();
        if(mCircle!=null) mCircle.remove();
        markerLocation=null;
        mPlaceMarker = null;
        mCircle = null;
        mRadius = 30;
    }

    void setItemSelectedView(){
        fab.setImageResource(R.drawable.ic_close);
        fab.setOnClickListener(v->{
            mPlaceListAdapter.removeSelection();
            setPlaceListView();
        });
    }

    boolean checkTextValues(){
        if(ed_place_name.getText().toString().isEmpty()) {
            showToast(R.string.place_neme_is_empty);
            return false;
        }
        return true;
    }

    void setTextRadius(int radius){
        runOnUiThread(()->{
            tv_radius.setText(String.format("%d м",radius));
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        myLocation = new LatLng(43.2131782,76.9133051);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,12));
        mMap.setOnMapClickListener(latLng -> {
            //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    latLng, 17);
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
            mMap.animateCamera(location, 1000, null);
            if(currentViewState == RADIUS_VIEW) {
                markerLocation = latLng;
                drawMarker(latLng);
                drawCircle(mRadius);
            }
        });
        presenter.getPlaces();
        //  mMap.animateCamera(CameraUpdateFactory.zoomTo(14));//
    }

    private void movaCamera(LatLng latLng , int min_zoom , int final_zoom){
        LatLng cameraPosition = mMap.getCameraPosition().target;
        int animationTime = 2000;
        double distance = MapHelper.getDistance(cameraPosition, latLng);
        if(distance>2000) {
            LatLng center = MapHelper.centerBetweenTwoPoints(cameraPosition, latLng);
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    center, min_zoom);
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
            mMap.animateCamera(location, animationTime / 2, null);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(()->{
                        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                                latLng, final_zoom);
                        mMap.animateCamera(location, (animationTime) /2, null);
                    });
                }
            }, animationTime / 2);
        }
        else{
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    latLng, final_zoom);
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
            mMap.animateCamera(location, animationTime, null);
        }
    }


    private void drawMarker(LatLng latLng) {
        if(mPlaceMarker==null) {
            mPlaceMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(getMarkerIcon("#000000"))
            );
            isMarkerSet = true;
            sb_radius.setEnabled(true);
        }
        else {
            mPlaceMarker.setPosition(latLng);
        }
    }

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    Circle mCircle ;
    void drawCircle(LatLng latLng , int radius){
        if(mCircle !=null){
            mCircle.setCenter(latLng);
            mCircle.setRadius(radius);
        }
        else {
            mCircle =  mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(radius)
                    .strokeWidth(1)
                    .strokeColor(Color.parseColor("#22000000"))
                    .fillColor(Color.parseColor("#22ed7474")));
        }
        }

        void drawCircle(int mRadius){
            if(markerLocation!=null &&mPlaceMarker!=null)
                drawCircle(markerLocation , mRadius);
        }

    public void setRadiusViewValues(int radiusViewValues) {
        setTextRadius(radiusViewValues);
        setRadiusProgress(radiusViewValues);
    }

    private void setRadiusProgress(int radiusViewValues) {
        runOnUiThread(()->{
            sb_radius.setProgress(radiusViewValues);
        });
    }

    @Override
    public void onPlacesLoaded(List<Place> places) {
        if(places.isEmpty()){
            setPlaceRadiusView();
        }
        else{
            setPlaceListView();
            mPlaceListAdapter.updateData(places);
        }
    }

    @Override
    public void onPlacesLoadError(int error_message) {
        showToast(error_message);
    }

    @Override
    public void hideLoadingDialog() {
        super.hideProgressDialog();
    }

    @Override
    public void showLoadingDialog() {
        super.showLoadingDialog();
    }


    @Override
    public void onPlaceSaveError(int error) {
//        showToast(R.string.unable_to_save_place);
        showToast(error);
    }

    @Override
    public void onPlaceSaved(Place place) {
        mPlaceListAdapter.add(place);
        setPlaceListView();
        clearEditTextViews();
    }

    @Override
    public void onPlaceDeleteError(int error)
    {
//            showToast(R.string.place_delete_failed);
        showToast(error);
    }

    @Override
    public void onPlaceDeleteSuccess(int id) {
        mPlaceListAdapter.removeByDataId(id);
        setPlaceListView();
    }

    @Override
    public void onPlaceEditSuccess(Place place, int edit_place_id) {
        mPlaceListAdapter.removeByDataId(edit_place_id);
        mPlaceListAdapter.add(place);
        setPlaceListView();
        clearEditTextViews();
        this.edit_place_id = -1;
    }

    @Override
    public void onPlaceEditError(int error) {
        showToast(error);
        setPlaceListView();
        clearEditTextViews();
    }

    private void clearEditTextViews() {
        ed_place_name.setText("");
        ed_place_description.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemSelected(Place data) {
        LatLng latLng = MapHelper.convertDoubleLatLng(data.getLat(), data.getLng() );
        if(latLng!=null) {
            markerLocation =latLng;
            drawMarker(latLng);
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            //  mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
//            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
//                    latLng, 15);
//            mMap.animateCamera(location, 1000, null);
            movaCamera(latLng,12,15);
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
            mRadius = data.getRadius();
            drawCircle(data.getRadius());
        }
        setItemSelectedView();
    }

    @Override
    public void onItemDelete(Place place) {
        if(place!=null && presenter!=null){
            showAreYouSureDialog(getString(R.string.are_you_sure_delete), new CustomSimpleDialog.Callback() {
                @Override
                public void onCancelBtnClicked() {
                    showToast(R.string.cancelled);
                    dissmissAreYouSureDialog();
                }

                @Override
                public void onPositiveBtnClicked() {
                    presenter.deletePlace(place.getId());
                    dissmissAreYouSureDialog();
                }
            });
        }
    }

    @Override
    public void onItemEdit(Place place) {
        if(place!=null && presenter!=null){
//            showToast("EDIT PLACE");
            setEditPlaceRadiusView(place);

        }
    }

    private void setEditPlaceRadiusView(Place place) {
        clearMapElements();
        findViewById(R.id.map).setVisibility(View.VISIBLE);
        ll_place_text_values_input_view.setVisibility(View.GONE);
        ll_place_list_view.setVisibility(View.GONE);
        currentViewState = RADIUS_VIEW;
        ll_radius_picker.setVisibility(View.VISIBLE);
        sb_radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mRadius = i + 1;
                drawCircle(mRadius);
                setTextRadius(mRadius);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        mRadius = place.getRadius();
        sb_radius.setProgress(mRadius);
        configureFabButton(R.drawable.ic_arrow_forward ,onRadiusViewButtonClick );
        ed_place_name.setText(place.getName());
        //markerLocation
        markerLocation = new LatLng(place.getLat(),place.getLng());
        drawMarker(markerLocation);
        drawCircle(mRadius);
        edit_place_id = place.getId();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(currentViewState==UNKNOWN_VIEW || currentViewState==LIST_VIEW) {
            super.onBackPressed();
        }
        else if(currentViewState == RADIUS_VIEW){
            if(mPlaceListAdapter.getItemCount()>0)
                setPlaceListView();
            else
                super.onBackPressed();
        }
        else if(currentViewState==TEXT_INFO_VIEW){
            setPlaceRadiusView();
        }
    }
}