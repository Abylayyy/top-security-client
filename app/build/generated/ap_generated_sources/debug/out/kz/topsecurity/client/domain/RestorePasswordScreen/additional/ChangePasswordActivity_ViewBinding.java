// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.domain.RestorePasswordScreen.additional;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditText;

public class ChangePasswordActivity_ViewBinding implements Unbinder {
  private ChangePasswordActivity target;

  @UiThread
  public ChangePasswordActivity_ViewBinding(ChangePasswordActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ChangePasswordActivity_ViewBinding(ChangePasswordActivity target, View source) {
    this.target = target;

    target.ed_new_password = Utils.findRequiredViewAsType(source, R.id.ed_new_password, "field 'ed_new_password'", RoundCorneredEditText.class);
    target.tv_new_password_error = Utils.findRequiredViewAsType(source, R.id.tv_new_password_error, "field 'tv_new_password_error'", TextView.class);
    target.ed_repeat_new_password = Utils.findRequiredViewAsType(source, R.id.ed_repeat_new_password, "field 'ed_repeat_new_password'", RoundCorneredEditText.class);
    target.tv_repeat_new_password_error = Utils.findRequiredViewAsType(source, R.id.tv_repeat_new_password_error, "field 'tv_repeat_new_password_error'", TextView.class);
    target.btn_confirm = Utils.findRequiredViewAsType(source, R.id.btn_confirm, "field 'btn_confirm'", Button.class);
    target.iv_back = Utils.findRequiredViewAsType(source, R.id.iv_back, "field 'iv_back'", ConstraintLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ChangePasswordActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.ed_new_password = null;
    target.tv_new_password_error = null;
    target.ed_repeat_new_password = null;
    target.tv_repeat_new_password_error = null;
    target.btn_confirm = null;
    target.iv_back = null;
  }
}
