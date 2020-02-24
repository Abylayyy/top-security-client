package kz.topsecurity.client.fragments.main_fragment.private_info;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import kz.topsecurity.client.R;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.utils.GlideApp;

public class SettingsFragment extends Fragment {

    LocationManager locationManager;
    public SettingsFragment() { }

    @BindView(R.id.backSecurity) LinearLayout back;
    @BindView(R.id.profPageImage) CircleImageView profImage;
    @BindView(R.id.switchGPS) Switch switchGps;
    @BindView(R.id.switchNotification) Switch switchNot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setClientAvatar();

        switchNot.setChecked(allowNotification());
        switchGps.setChecked(allowGps());

        back.setOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());

        switchGps.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                if (!allowGps()) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
            }
        });
    }

    private boolean allowNotification() {
        return NotificationManagerCompat.from(getContext()).areNotificationsEnabled();
    }



    private boolean allowGps() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    private void setClientAvatar() {
        DataBaseManager dataBaseManager = new DataBaseManagerImpl(getActivity());
        String userAvatar = null;
        Client clientData = dataBaseManager.getClientData();

        if(clientData!=null){
            userAvatar = clientData.getPhoto();
        }
        GlideApp.with(getActivity())
                .load(userAvatar)
                .placeholder(R.drawable.big_prof_image)
                .into(profImage);
    }
}
