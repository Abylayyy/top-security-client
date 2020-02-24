package kz.topsecurity.client.fragments.main_fragment.private_info.info_main;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import kz.topsecurity.client.BuildConfig;
import kz.topsecurity.client.R;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.utils.GlideApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutAppFragment extends Fragment {
    @BindView(R.id.backSecurity) LinearLayout back;
    @BindView(R.id.versiontxt) TextView version;
    @BindView(R.id.profPageImage)
    CircleImageView profImage;

    String versia = BuildConfig.VERSION_NAME;
    private String uri;

    public AboutAppFragment() {
        // Required empty public constructor
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_app, container, false);
        ButterKnife.bind(this, view);

        setClientAvatar();

        version.setText(versia + " beta");

        back.setOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());


        return view;
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
