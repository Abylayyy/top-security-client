// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.domain.StartScreen;

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
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextWithPhoneMask;

public class StartActivity_ViewBinding implements Unbinder {
  private StartActivity target;

  @UiThread
  public StartActivity_ViewBinding(StartActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public StartActivity_ViewBinding(StartActivity target, View source) {
    this.target = target;

    target.tv_phone_number_error = Utils.findRequiredViewAsType(source, R.id.tv_phone_number_error, "field 'tv_phone_number_error'", TextView.class);
    target.tv_password_error = Utils.findRequiredViewAsType(source, R.id.tv_password_error, "field 'tv_password_error'", TextView.class);
    target.cl_loading_layer = Utils.findRequiredViewAsType(source, R.id.cl_login_loading_layer, "field 'cl_loading_layer'", ConstraintLayout.class);
    target.cl_login_layer = Utils.findRequiredViewAsType(source, R.id.cl_login_layer, "field 'cl_login_layer'", ConstraintLayout.class);
    target.btn_sign_in = Utils.findRequiredViewAsType(source, R.id.btn_sign_in, "field 'btn_sign_in'", Button.class);
    target.btn_sign_up = Utils.findRequiredViewAsType(source, R.id.btn_sign_up, "field 'btn_sign_up'", TextView.class);
    target.tv_forget_password = Utils.findRequiredViewAsType(source, R.id.tv_forget_password, "field 'tv_forget_password'", TextView.class);
    target.ed_tel_number = Utils.findRequiredViewAsType(source, R.id.ed_tel_number, "field 'ed_tel_number'", RoundCorneredEditTextWithPhoneMask.class);
    target.ed_password = Utils.findRequiredViewAsType(source, R.id.ed_password, "field 'ed_password'", RoundCorneredEditText.class);
    target.number_hide = Utils.findRequiredViewAsType(source, R.id.phone_number_hide, "field 'number_hide'", RoundCorneredEditText.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    StartActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.tv_phone_number_error = null;
    target.tv_password_error = null;
    target.cl_loading_layer = null;
    target.cl_login_layer = null;
    target.btn_sign_in = null;
    target.btn_sign_up = null;
    target.tv_forget_password = null;
    target.ed_tel_number = null;
    target.ed_password = null;
    target.number_hide = null;
  }
}
