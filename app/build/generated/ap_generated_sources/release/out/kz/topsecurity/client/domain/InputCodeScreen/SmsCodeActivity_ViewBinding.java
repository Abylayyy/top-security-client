// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.domain.InputCodeScreen;

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

public class SmsCodeActivity_ViewBinding implements Unbinder {
  private SmsCodeActivity target;

  @UiThread
  public SmsCodeActivity_ViewBinding(SmsCodeActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SmsCodeActivity_ViewBinding(SmsCodeActivity target, View source) {
    this.target = target;

    target.iv_back = Utils.findRequiredViewAsType(source, R.id.iv_back, "field 'iv_back'", ConstraintLayout.class);
    target.cl_code = Utils.findRequiredViewAsType(source, R.id.cl_code, "field 'cl_code'", ConstraintLayout.class);
    target.tv_sms_code_label = Utils.findRequiredViewAsType(source, R.id.tv_sms_code_label, "field 'tv_sms_code_label'", TextView.class);
    target.ed_sms_code = Utils.findRequiredViewAsType(source, R.id.ed_sms_code, "field 'ed_sms_code'", RoundCorneredEditText.class);
    target.tv_sms_code_error = Utils.findRequiredViewAsType(source, R.id.tv_sms_code_error, "field 'tv_sms_code_error'", TextView.class);
    target.tv_send_again = Utils.findRequiredViewAsType(source, R.id.tv_send_again, "field 'tv_send_again'", TextView.class);
    target.btn_confirm = Utils.findRequiredViewAsType(source, R.id.btn_confirm, "field 'btn_confirm'", Button.class);
    target.count_down = Utils.findRequiredViewAsType(source, R.id.count_down_time, "field 'count_down'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SmsCodeActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.iv_back = null;
    target.cl_code = null;
    target.tv_sms_code_label = null;
    target.ed_sms_code = null;
    target.tv_sms_code_error = null;
    target.tv_send_again = null;
    target.btn_confirm = null;
    target.count_down = null;
  }
}
