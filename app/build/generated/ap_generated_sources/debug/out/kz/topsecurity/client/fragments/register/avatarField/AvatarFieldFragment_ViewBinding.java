// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.fragments.register.avatarField;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;

public class AvatarFieldFragment_ViewBinding implements Unbinder {
  private AvatarFieldFragment target;

  @UiThread
  public AvatarFieldFragment_ViewBinding(AvatarFieldFragment target, View source) {
    this.target = target;

    target.rl_user_avatar = Utils.findRequiredViewAsType(source, R.id.rl_user_avatar, "field 'rl_user_avatar'", RelativeLayout.class);
    target.iv_user_avatar = Utils.findRequiredViewAsType(source, R.id.iv_user_avatar, "field 'iv_user_avatar'", ImageView.class);
    target.cb_privacy_policy = Utils.findRequiredViewAsType(source, R.id.cb_privacy_policy, "field 'cb_privacy_policy'", CheckBox.class);
    target.btn_sign_up = Utils.findRequiredViewAsType(source, R.id.btn_sign_up, "field 'btn_sign_up'", Button.class);
    target.tv_privacy_policy = Utils.findRequiredViewAsType(source, R.id.tv_privacy_policy, "field 'tv_privacy_policy'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AvatarFieldFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.rl_user_avatar = null;
    target.iv_user_avatar = null;
    target.cb_privacy_policy = null;
    target.btn_sign_up = null;
    target.tv_privacy_policy = null;
  }
}
