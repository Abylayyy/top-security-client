package kz.topsecurity.client.domain.PlaceScreen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import kz.topsecurity.client.R;
import kz.topsecurity.client.domain.PlaceScreen.adapter.PlaceListAdapter;
import kz.topsecurity.client.domain.base.BaseActivity;
import kz.topsecurity.client.helper.MapHelper;
import kz.topsecurity.client.helper.ResizeAnimation;
import kz.topsecurity.client.model.place.Place;
import kz.topsecurity.client.presenter.placesPresenter.PlacesPresenter;
import kz.topsecurity.client.presenter.placesPresenter.PlacesPresenterImpl;
import kz.topsecurity.client.view.placesView.PlacesView;

public class PlaceActivity
        extends BaseActivity<PlacesView,
        PlacesPresenter,
        PlacesPresenterImpl>
        implements OnMapReadyCallback, PlacesView,
        PlaceListAdapter.PlaceListAdapterListener {

    private GoogleMap mMap;
    private LatLng markerLocation, currentLocation;
    private Marker mPlaceMarker;
    private int mRadius = 5;
    boolean isMarkerSet = false;
    FusedLocationProviderClient providerClient;
    ArrayAdapter<String> adapter;
    List<Marker> markerList = new ArrayList<>();

    @BindView(R.id.backLinear) LinearLayout back;
    @BindView(R.id.addMapIcon) FloatingActionButton addPlaceIcon;
    @BindView(R.id.favorIcon) FloatingActionButton favorIcon;
    @BindView(R.id.gpsIcon) FloatingActionButton gpsIcon;
    @BindView(R.id.searchTxt) AutoCompleteTextView searchTxt;
    @BindView(R.id.searchIcon) CardView searchIcon;

    @BindView(R.id.addFavorBottomSheet) ConstraintLayout addFavorBottom;
    @BindView(R.id.nameOfPlace) EditText placeName;
    @BindView(R.id.favorNameView) View favorError;
    @BindView(R.id.savePlace) TextView savePlace;

    @BindView(R.id.favorListBottomSheet) ConstraintLayout favorListBottom;
    @BindView(R.id.favorRecycler) RecyclerView favorRecycler;
    public static TextView saveFavor;

    int currentViewState = 1;
    private static final int ADD_VIEW = 464;
    private static final int SEARCH_VIEW = 264;
    private static final int LIST_VIEW = 164;
    boolean addState = false, listState = false;
    int edit_place_id = -1;
    int searchWidth;

    private List<AutocompletePrediction> predictionList;

    private PlacesClient placesClient;

    PlaceListAdapter mPlaceListAdapter = new PlaceListAdapter(new ArrayList<>(), this);
    private RecyclerView.LayoutManager mLayoutManager;
    BottomSheetBehavior addBehavior, listBehavior;
    List<String> suggestionsList = new ArrayList<>();

    BottomSheetDialog addDialog, listDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        ButterKnife.bind(this);
        initPresenter(new PlacesPresenterImpl(this));

        saveFavor = findViewById(R.id.saveChangeFavor);

        saveFavor.setOnClickListener(v -> {
            if (saveFavor.getText().equals("Изменить")) {
                changeProfInfo();
            } else {
                saveAllInfo();
            }
        });

        searchWidth = searchIcon.getWidth();

        com.google.android.libraries.places.api.Places.initialize(this, getString(R.string.google_maps_key));
        placesClient = Places.createClient(getApplication());
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();


        addBehavior = BottomSheetBehavior.from(addFavorBottom);
        listBehavior = BottomSheetBehavior.from(favorListBottom);

        addBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        listBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        if (addBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            currentViewState = 1;
        }
        if (listBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            currentViewState = 1;
        }

        addDialog = new BottomSheetDialog(this);
        addDialog.setContentView(R.layout.bottom_favor_add);

        listDialog = new BottomSheetDialog(this);
        listDialog.setContentView(R.layout.bottom_favor_list);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (checkRadiusData()) {
            showAddFavor();
        }

        back.setOnClickListener(v -> onBackPressed());
        searchIcon.setOnClickListener(v -> showSearchWidget());

        favorIcon.setOnClickListener(v -> {
            if (!listState) {
                showFavorBottom();
            } else {
                hideFavorBottom();
            }
        });
        gpsIcon.setOnClickListener(v -> {
            if (isGPSEnabled(getApplication())) {
                getCurrentLocation();
            } else {
                turnOnGps();
            }
        });

        addPlaceIcon.setOnClickListener(v -> {
            if (!addState) {
                showAddFavor();
            } else {
                hideAddFavor();
            }
        });

        savePlace.setOnClickListener(v -> {
            if (checkTextValues()) {
                if (edit_place_id == -1) {
                    if (checkPlaces(placeName.getText().toString(), markerLocation)) {
                        presenter.savePlace(placeName.getText().toString(), markerLocation, "", 0);
                        drawFavorMarker(currentLocation);
                        clearMapElements();
                        hideAddFavor();
                    }
                }
            }
        });

        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setCountry("KZ")
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();

                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                        if (predictionsResponse != null) {
                            predictionList = predictionsResponse.getAutocompletePredictions();
                            for (int i = 0; i < predictionList.size(); i++) {
                                AutocompletePrediction prediction = predictionList.get(i);
                                suggestionsList.add(prediction.getFullText(null).toString());
                            }
                        }
                    } else {
                        Log.i("mytag", "prediction fetching task unsuccessful");
                    }
                });

                adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, suggestionsList);
                searchTxt.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        setupRV();

        searchTxt.setDropDownWidth(getHeight() - 100);

        searchTxt.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= predictionList.size()) {
                return;
            }

            AutocompletePrediction selectedPrediction = predictionList.get(position);
            final String placeId = selectedPrediction.getPlaceId();

            List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(
                    com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

            FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
            placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(fetchPlaceResponse -> {
                com.google.android.libraries.places.api.model.Place place = fetchPlaceResponse.getPlace();
                Log.i("mytag", "Place found: " + place.getName());
                markerLocation = place.getLatLng();
                currentLocation = place.getLatLng();

                if (markerLocation != null) {
                    movaCamera(markerLocation, 12, 15);
                    drawMarker(markerLocation);
                }
            }).addOnFailureListener(e -> {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    apiException.printStackTrace();
                }
            });
        });
    }

    private void saveAllInfo() {
        mPlaceListAdapter.updateVisibility(false);
        saveFavor.setText("Изменить");
        saveFavor.setTextColor(Color.parseColor("#308313"));
    }

    private void changeProfInfo() {
        mPlaceListAdapter.updateVisibility(true);
        saveFavor.setText("Сохранить");
        saveFavor.setTextColor(Color.parseColor("#EF3B39"));
    }

    private int getHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private void showSearchWidget() {
        ResizeAnimation animation = new ResizeAnimation(searchIcon, getHeight() - back.getWidth() - 110);
        animation.setDuration(500);
        searchIcon.startAnimation(animation);
        new Handler().postDelayed(()->searchTxt.setVisibility(View.VISIBLE), 500);
        currentViewState = SEARCH_VIEW;
    }

    private void hideSearWidget() {
        ResizeAnimation animation = new ResizeAnimation(searchIcon, 147);
        animation.setDuration(500);
        searchIcon.startAnimation(animation);
        new Handler().postDelayed(()->searchTxt.setVisibility(View.GONE), 500);
        currentViewState = 1;
    }

    private boolean checkPlaces(String name, LatLng location) {
        boolean check = false;
        boolean[] checks = new boolean[]{false, false};
        if (name.equals("")) {
            favorError.setBackgroundColor(Color.parseColor("#EF3B39"));
        } else {
            checks[0] = true;
            favorError.setBackgroundColor(Color.parseColor("#b6b6b6"));
        }
        if (location == null) {
            Toasty.info(getApplication(), "Избранное место не выбрано", Toasty.LENGTH_SHORT).show();
        } else {
            checks[1] = true;
        }
        if (checks[0] && checks[1]) {
            check = true;
        }
        return check;
    }

    private void turnOnGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS не включен. Включить gps?");
        builder.setPositiveButton(R.string.yes, (dialog, id) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
        builder.create().show();
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton("ОК", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showAddFavor() {
        addBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        hideFavorBottom();
        addState = true;
        currentViewState = ADD_VIEW;
    }

    private void showFavorBottom() {
        listBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        hideAddFavor();
        listState = true;
        currentViewState = LIST_VIEW;
    }

    private void hideAddFavor() {
        addBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        addState = false;
        currentViewState = 1;
    }

    private void hideFavorBottom() {
        saveAllInfo();
        listBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        listState = false;
        currentViewState = 1;
    }

    public boolean isGPSEnabled(Context mContext) {
        LocationManager lm = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void getCurrentLocation() {
        providerClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        Task location = providerClient.getLastLocation();
        location.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location currentL = (Location) task.getResult();
                LatLng latLng = null;
                if (currentL != null) {
                    latLng = new LatLng(currentL.getLatitude(), currentL.getLongitude());
                }
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    mMap.animateCamera(cameraUpdate, 1000, null);
                }, 3000);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void setupRV() {
        favorRecycler.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        favorRecycler.setLayoutManager(mLayoutManager);
        favorRecycler.setAdapter(mPlaceListAdapter);
    }

    void setPlaceRadiusView() {
        clearMapElements();
        findViewById(R.id.map).setVisibility(View.VISIBLE);
    }

    boolean checkRadiusData() {
        if (markerLocation != null && mPlaceMarker != null)
            return true;
        showToast(R.string.choose_place);
        return false;
    }

    private void clearMapElements() {
        if (mPlaceMarker != null) mPlaceMarker.remove();
        if (mCircle != null) mCircle.remove();
        markerLocation = null;
        mPlaceMarker = null;
        mCircle = null;
        mRadius = 10;
    }

    boolean checkTextValues() {
        if (placeName.getText().toString().isEmpty()) {
            showToast(R.string.place_neme_is_empty);
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng myLocation = new LatLng(43.2131782, 76.9133051);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12));
        mMap.setOnMapClickListener(latLng -> {
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                    latLng, 17);
            mMap.animateCamera(location, 1000, null);
            markerLocation = latLng;
            currentLocation = latLng;
            drawMarker(latLng);
        });
        presenter.getPlaces();
    }

    private void movaCamera(LatLng latLng, int min_zoom, int final_zoom) {
        LatLng cameraPosition = mMap.getCameraPosition().target;
        int animationTime = 2000;
        double distance = MapHelper.getDistance(cameraPosition, latLng);
        if (distance > 2000) {
            LatLng center = MapHelper.centerBetweenTwoPoints(cameraPosition, latLng);
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(center, min_zoom);
            mMap.animateCamera(location, animationTime / 2, null);

            new Handler().postDelayed(() -> runOnUiThread(() -> {
                CameraUpdate location1 = CameraUpdateFactory.newLatLngZoom(latLng, final_zoom);
                mMap.animateCamera(location1, (animationTime) / 2, null);
            }), animationTime / 2);
        } else {
            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, final_zoom);
            mMap.animateCamera(location, animationTime, null);
        }
    }


    private void drawMarker(LatLng latLng) {
        if (mPlaceMarker == null) {
            mPlaceMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(getMarkerIcon())

            );
            isMarkerSet = true;
        } else {
            mPlaceMarker.setPosition(latLng);
        }
    }

    private void drawFavorMarker(LatLng latLng) {
        mMap.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .icon(getFavorIcon())
        );
    }

    public BitmapDescriptor getFavorIcon() {
        BitmapDrawable bitmap = (BitmapDrawable) getResources().getDrawable(R.drawable.favor_place_icon);
        Bitmap bitmap1 = bitmap.getBitmap();
        Bitmap result = Bitmap.createScaledBitmap(bitmap1, 50, 50, false);
        return BitmapDescriptorFactory.fromBitmap(result);
    }

    public BitmapDescriptor getMarkerIcon() {
        BitmapDrawable bitmap = (BitmapDrawable) getResources().getDrawable(R.drawable.placeholder_map);
        Bitmap bitmap1 = bitmap.getBitmap();
        Bitmap result = Bitmap.createScaledBitmap(bitmap1, 44, 73, false);
        return BitmapDescriptorFactory.fromBitmap(result);
    }

    Circle mCircle;

    void drawCircle(LatLng latLng, int radius) {
        if (mCircle != null) {
            mCircle.setCenter(latLng);
            mCircle.setRadius(radius);
        } else {
            mCircle = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(radius)
                    .strokeWidth(1)
                    .strokeColor(Color.parseColor("#22000000"))
                    .fillColor(Color.parseColor("#22ed7474")));
        }
    }

    @Override
    public void onPlacesLoaded(List<Place> places) {
        if (places.isEmpty()) {
            setPlaceRadiusView();
        } else {
            mPlaceListAdapter.updateData(places);
            drawFavorMarkers();
        }
    }

    @Override
    public void onPlacesLoadError(int error_message) {
        showDialog(getString(error_message));
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
        showDialog(getString(error));
    }

    @Override
    public void onPlaceSaved(Place place) {
        mPlaceListAdapter.add(place);
        clearEditTextViews();
    }

    @Override
    public void onPlaceDeleteError(int error) {
        showToast(error);
    }

    @Override
    public void onPlaceDeleteSuccess(int id) {
        mPlaceListAdapter.removeByDataId(id);
        restartActivity();
    }

    private void restartActivity() {
        finish();
        overridePendingTransition( 0, 0);
        startActivity(getIntent());
        overridePendingTransition( 0, 0);
    }

    private void clearEditTextViews() {
        placeName.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemSelected(Place data) {
        LatLng latLng = MapHelper.convertDoubleLatLng(data.getLat(), data.getLng());
        if (latLng != null) {
            movaCamera(latLng, 12, 15);
        }
    }

    private void drawFavorMarkers() {
        List<Place> places = mPlaceListAdapter.getPlacesList();
        for (Place place : places) {
            LatLng latLng = MapHelper.convertDoubleLatLng(place.getLat(), place.getLng());
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .icon(getFavorIcon())
                    .title(place.getName());
            markerList.add(mMap.addMarker(options));
        }
    }

    @Override
    public void onItemDelete(Place place) {
        if (place != null && presenter != null) {
            presenter.deletePlace(place.getId());
        }
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
        if (currentViewState == 1) {
            super.onBackPressed();
        } else if (currentViewState == LIST_VIEW) {
            hideFavorBottom();
        } else if (currentViewState == SEARCH_VIEW) {
            hideSearWidget();
        } else if (currentViewState == ADD_VIEW) {
            hideAddFavor();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {}

}