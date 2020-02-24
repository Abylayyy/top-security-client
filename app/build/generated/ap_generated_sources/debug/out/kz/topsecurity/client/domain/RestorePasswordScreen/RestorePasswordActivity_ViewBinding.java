// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.domain.RestorePasswordScreen;

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

public class RestorePasswordActivity_ViewBinding implements Unbinder {
  private RestorePasswordActivity target;

  @UiThread
  public RestorePasswordActivity_ViewBinding(RestorePasswordActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public RestorePasswordActivity_ViewBinding(RestorePasswordActivity target, View source) {
    this.target = target;

    target.iv_back = Utils.findRequiredViewAsType(source, R.id.iv_back, "field 'iv_back'", ConstraintLayout.class);
    target.cl_restore_pass_layer = Utils.findRequiredViewAsType(source, R.id.cl_restore_pass_layer, "field 'cl_restore_pass_layer'", ConstraintLayout.class);
    target.cl_telephone = Utils.findRequiredViewAsType(source, R.id.cl_telephone, "field 'cl_telephone'", ConstraintLayout.class);
    target.cl_code = Utils.findRequiredViewAsType(source, R.id.cl_code, "field 'cl_code'", ConstraintLayout.class);
    target.cl_restore_pass_loading_layer = Utils.findRequiredViewAsType(source, R.id.cl_restore_pass_loading_layer, "field 'cl_restore_pass_loading_layer'", ConstraintLayout.class);
    target.tv_telephone_number_label = Utils.findRequiredViewAsType(source, R.id.tv_telephone_number_label, "field 'tv_telephone_number_label'", TextView.class);
    target.tv_phone_number_error = Utils.findRequiredViewAsType(source, R.id.tv_phone_number_error, "field 'tv_phone_number_error'", TextView.class);
    target.tv_sms_code_label = Utils.findRequiredViewAsType(source, R.id.tv_sms_code_label, "field 'tv_sms_code_label'", TextView.class);
    target.tv_sms_code_error = Utils.findRequiredViewAsType(source, R.id.tv_sms_code_error, "field 'tv_sms_code_error'", TextView.class);
    target.tv_send_again = Utils.findRequiredViewAsType(source, R.id.tv_send_again, "field 'tv_send_again'", TextView.class);
    target.ed_tel_number = Utils.findRequiredViewAsType(source, R.id.ed_tel_number, "field 'ed_tel_number'", RoundCorneredEditTextWithPhoneMask.class);
    target.ed_sms_code = Utils.findRequiredViewAsType(source, R.id.ed_sms_code, "field 'ed_sms_code'", RoundCorneredEditText.class);
    target.btn_confirm = Utils.findRequiredViewAsType(source, R.id.btn_confirm, "field 'btn_confirm'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    RestorePasswordActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.iv_back = null;
    target.cl_restore_pass_layer = null;
    target.cl_telephone = null;
    target.cl_code = null;
    target.cl_restore_pass_loading_layer = null;
    target.tv_telephone_number_label = null;
    target.tv_phone_number_error = null;
    target.tv_sms_code_label = null;
    target.tv_sms_code_error = null;
    target.tv_send_again = null;
    target.ed_tel_number = null;
    target.ed_sms_code = null;
    target.btn_confirm = null;
  }
}
