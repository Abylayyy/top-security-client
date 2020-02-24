// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.fragments.register.signUpFields;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditText;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextWithPhoneMask;

public class RegisterSignFieldsFragment_ViewBinding implements Unbinder {
  private RegisterSignFieldsFragment target;

  @UiThread
  public RegisterSignFieldsFragment_ViewBinding(RegisterSignFieldsFragment target, View source) {
    this.target = target;

    target.ed_tel_number = Utils.findRequiredViewAsType(source, R.id.ed_tel_number, "field 'ed_tel_number'", RoundCorneredEditTextWithPhoneMask.class);
    target.tv_phone_number_error = Utils.findRequiredViewAsType(source, R.id.tv_phone_number_error, "field 'tv_phone_number_error'", TextView.class);
    target.ed_password = Utils.findRequiredViewAsType(source, R.id.ed_password, "field 'ed_password'", RoundCorneredEditText.class);
    target.tv_password_error = Utils.findRequiredViewAsType(source, R.id.tv_password_error, "field 'tv_password_error'", TextView.class);
    target.ed_confirm_password = Utils.findRequiredViewAsType(source, R.id.ed_confirm_password, "field 'ed_confirm_password'", RoundCorneredEditText.class);
    target.tv_confirm_password_error = Utils.findRequiredViewAsType(source, R.id.tv_confirm_password_error, "field 'tv_confirm_password_error'", TextView.class);
    target.ed_num_hide = Utils.findRequiredViewAsType(source, R.id.ed_num_hide, "field 'ed_num_hide'", RoundCorneredEditText.class);
    target.infText = Utils.findRequiredViewAsType(source, R.id.infText, "field 'infText'", TextView.class);
    target.checkAgree = Utils.findRequiredViewAsType(source, R.id.checkAgree, "field 'checkAgree'", CheckBox.class);
    target.btn_sign_up = Utils.findRequiredViewAsType(source, R.id.btn_sign_up, "field 'btn_sign_up'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    RegisterSignFieldsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.ed_tel_number = null;
    target.tv_phone_number_error = null;
    target.ed_password = null;
    target.tv_password_error = null;
    target.ed_confirm_password = null;
    target.tv_confirm_password_error = null;
    target.ed_num_hide = null;
    target.infText = null;
    target.checkAgree = null;
    target.btn_sign_up = null;
  }
}
