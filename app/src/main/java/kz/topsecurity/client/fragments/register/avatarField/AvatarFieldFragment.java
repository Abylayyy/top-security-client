package kz.topsecurity.client.fragments.register.avatarField;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.topsecurity.client.R;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.presenter.registerPresenter.fragmentsPresenter.AvatarFieldPresenter;
import kz.topsecurity.client.presenter.registerPresenter.fragmentsPresenter.AvatarFieldPresenterImpl;
import kz.topsecurity.client.utils.GlideApp;
import kz.topsecurity.client.view.registerView.fragmentsView.AvatarFieldView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;

public class AvatarFieldFragment extends Fragment implements AvatarFieldView {

    @BindView(R.id.rl_user_avatar)
    RelativeLayout rl_user_avatar;
    @BindView(R.id.iv_user_avatar)
    ImageView iv_user_avatar;
    @BindView(R.id.cb_privacy_policy)
    CheckBox cb_privacy_policy;
    @BindView(R.id.btn_sign_up)
    Button btn_sign_up;
    @BindView(R.id.tv_privacy_policy)
    TextView tv_privacy_policy;

    AvatarFieldFragmentCallback mCallback = null;
    AvatarFieldPresenter presenter;
    Bitmap avatar = null;

    public interface AvatarFieldFragmentCallback{
        void onLoadAvatarRequest();
        void onClosed();

        void showToast(int make_agreement_to_privacy_policy);

        void onSignUp();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (AvatarFieldFragmentCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public static AvatarFieldFragment newInstance() {
        return new AvatarFieldFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.avatar_field_fragment, container, false);
        ButterKnife.bind(this,inflate);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new AvatarFieldPresenterImpl(this);
        rl_user_avatar.setOnClickListener(v->{
            if(mCallback!=null)
                mCallback.onLoadAvatarRequest();
        });
        btn_sign_up.setOnClickListener(v->{
            if(cb_privacy_policy.isChecked()){
                if(avatar!=null)
                {
                    mCallback.onSignUp();
                }
                else{
                    mCallback.showToast(R.string.need_avatar_to_work_properly_register);
                }
            }
            else{
                mCallback.showToast(R.string.make_agreement_to_privacy_policy);
            }
        });
        tv_privacy_policy.setOnClickListener(v->{
            openPrivacyPolicyLink();
        });
        GlideApp.with(this).load(R.drawable.placeholder_avatar).into(iv_user_avatar);
    }

    private void openPrivacyPolicyLink() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(Constants.PRIVACY_POLICY_LINK));
        startActivity(i);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

    public void setImage(Bitmap bitmap){
        avatar = bitmap;
        if(getContext()!=null)
            GlideApp.with(getContext()).load(bitmap).into(iv_user_avatar);
    }
    @Override
    public void onPause() {
        super.onPause();
        if(presenter!=null)
            presenter.detach();
    }

}
