package kz.topsecurity.client.fragments.main_fragment.private_info;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import kz.topsecurity.client.R;
import kz.topsecurity.client.fragments.main_fragment.private_info.info_main.AboutAppFragment;
import kz.topsecurity.client.fragments.main_fragment.private_info.info_main.InstructionFragment;
import kz.topsecurity.client.fragments.main_fragment.private_info.info_main.UserConfidenceFragment;
import kz.topsecurity.client.helper.dataBase.DataBaseManager;
import kz.topsecurity.client.helper.dataBase.DataBaseManagerImpl;
import kz.topsecurity.client.model.other.Client;
import kz.topsecurity.client.utils.GlideApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {
    private String uri;

    public InfoFragment() { }

    @BindView(R.id.backSecurity) LinearLayout back;
    @BindView(R.id.aboutApp) TextView about;
    @BindView(R.id.userConf) TextView userConf;
    @BindView(R.id.instruction) TextView instruction;
    @BindView(R.id.beginLearning) TextView beginLearning;
    @BindView(R.id.emailSupport) TextView email;
    @BindView(R.id.dialNum) TextView dial;
    @BindView(R.id.profPageImage)
    CircleImageView profImage;
    Fragment currentFragment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.bind(this, view);

        setClientAvatar();

        back.setOnClickListener(v -> Objects.requireNonNull(getActivity()).onBackPressed());

        about.setOnClickListener(v -> {
            currentFragment = new AboutAppFragment();
            replaceFragment(currentFragment, "aboutApp", true);
        });

        userConf.setOnClickListener(v -> {
            currentFragment = new UserConfidenceFragment();
            replaceFragment(currentFragment, "userConf", true);

        });

        instruction.setOnClickListener(v -> {
            currentFragment = new InstructionFragment();
            replaceFragment(currentFragment, "instruction", true);
        });

        dial.setOnClickListener(v -> {
            String content = dial.getText().toString();
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                    "tel", content, null));
            startActivity(phoneIntent);
        });

        email.setOnClickListener(v -> {
            String content = email.getText().toString();
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + content));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Технический вопрос");
            intent.putExtra(Intent.EXTRA_TEXT, "Здесь напишите свою проблему");
            startActivity(Intent.createChooser(intent, "Отправить Email"));
        });

        beginLearning.setOnClickListener(v -> {
            Toasty.info(getContext(), getString(R.string.not_ready), Toasty.LENGTH_SHORT).show();
        });

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

    private static final String BACK_STACK_ROOT = "previousFragment";

    public void replaceFragment(Fragment fragment, String fragmentTag, boolean withBackStack){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        FragmentTransaction transaction  = getActivity().getSupportFragmentManager().beginTransaction();
        if(withBackStack){
            fragmentManager.popBackStack(BACK_STACK_ROOT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            transaction.addToBackStack(BACK_STACK_ROOT);
        }
        transaction.replace(R.id.mainBottomContainer, fragment,fragmentTag);
        transaction.commit();
    }
}
