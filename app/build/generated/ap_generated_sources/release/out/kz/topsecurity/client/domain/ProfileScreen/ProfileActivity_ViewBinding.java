// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.domain.ProfileScreen;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;

public class ProfileActivity_ViewBinding implements Unbinder {
  private ProfileActivity target;

  @UiThread
  public ProfileActivity_ViewBinding(ProfileActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ProfileActivity_ViewBinding(ProfileActivity target, View source) {
    this.target = target;

    target.tv_delete_account = Utils.findRequiredViewAsType(source, R.id.tv_delete_account, "field 'tv_delete_account'", TextView.class);
    target.cl_profile_view = Utils.findRequiredViewAsType(source, R.id.cl_profile_view, "field 'cl_profile_view'", ConstraintLayout.class);
    target.cl_delete_view = Utils.findRequiredViewAsType(source, R.id.cl_delete_view, "field 'cl_delete_view'", ConstraintLayout.class);
    target.btn_ok = Utils.findRequiredViewAsType(source, R.id.btn_ok, "field 'btn_ok'", Button.class);
    target.btn_cancel = Utils.findRequiredViewAsType(source, R.id.btn_cancel, "field 'btn_cancel'", Button.class);
    target.rl_user_avatar = Utils.findRequiredViewAsType(source, R.id.rl_user_avatar, "field 'rl_user_avatar'", RelativeLayout.class);
    target.tv_edit_user_email = Utils.findRequiredViewAsType(source, R.id.tv_edit_user_email, "field 'tv_edit_user_email'", TextView.class);
    target.tv_edit_user_password = Utils.findRequiredViewAsType(source, R.id.tv_edit_user_password, "field 'tv_edit_user_password'", TextView.class);
    target.iv_user_avatar = Utils.findRequiredViewAsType(source, R.id.iv_user_avatar, "field 'iv_user_avatar'", CircleImageView.class);
    target.tv_add_secret_code = Utils.findRequiredViewAsType(source, R.id.tv_add_secret_code, "field 'tv_add_secret_code'", TextView.class);
    target.iv_upload_avatar = Utils.findRequiredViewAsType(source, R.id.iv_upload_avatar, "field 'iv_upload_avatar'", ImageView.class);
    target.tv_send_avatar_to_server = Utils.findRequiredViewAsType(source, R.id.tv_send_avatar_to_server, "field 'tv_send_avatar_to_server'", TextView.class);
    target.tv_user_iin = Utils.findRequiredViewAsType(source, R.id.tv_user_iin, "field 'tv_user_iin'", TextView.class);
    target.tv_user_full_name = Utils.findRequiredViewAsType(source, R.id.tv_user_full_name, "field 'tv_user_full_name'", TextView.class);
    target.tv_phone = Utils.findRequiredViewAsType(source, R.id.tv_phone, "field 'tv_phone'", TextView.class);
    target.tv_edit_user_healthcard = Utils.findRequiredViewAsType(source, R.id.tv_edit_user_healthcard, "field 'tv_edit_user_healthcard'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ProfileActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.tv_delete_account = null;
    target.cl_profile_view = null;
    target.cl_delete_view = null;
    target.btn_ok = null;
    target.btn_cancel = null;
    target.rl_user_avatar = null;
    target.tv_edit_user_email = null;
    target.tv_edit_user_password = null;
    target.iv_user_avatar = null;
    target.tv_add_secret_code = null;
    target.iv_upload_avatar = null;
    target.tv_send_avatar_to_server = null;
    target.tv_user_iin = null;
    target.tv_user_full_name = null;
    target.tv_phone = null;
    target.tv_edit_user_healthcard = null;
  }
}
