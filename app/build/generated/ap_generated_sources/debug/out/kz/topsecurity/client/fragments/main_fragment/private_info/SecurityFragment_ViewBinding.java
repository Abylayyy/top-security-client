// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.fragments.main_fragment.private_info;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditText;

public class SecurityFragment_ViewBinding implements Unbinder {
  private SecurityFragment target;

  @UiThread
  public SecurityFragment_ViewBinding(SecurityFragment target, View source) {
    this.target = target;

    target.changePassword = Utils.findRequiredViewAsType(source, R.id.changePassInfo, "field 'changePassword'", TextView.class);
    target.back = Utils.findRequiredViewAsType(source, R.id.backSecurity, "field 'back'", LinearLayout.class);
    target.erCurrent = Utils.findRequiredViewAsType(source, R.id.errCurrentPass, "field 'erCurrent'", TextView.class);
    target.erNew = Utils.findRequiredViewAsType(source, R.id.errNewPass, "field 'erNew'", TextView.class);
    target.erRepeat = Utils.findRequiredViewAsType(source, R.id.errRepeatPass, "field 'erRepeat'", TextView.class);
    target.deletePin = Utils.findRequiredViewAsType(source, R.id.deletePin, "field 'deletePin'", TextView.class);
    target.currPass = Utils.findRequiredViewAsType(source, R.id.changeCurrentPass, "field 'currPass'", RoundCorneredEditText.class);
    target.newCurrPass = Utils.findRequiredViewAsType(source, R.id.newCurrentPass, "field 'newCurrPass'", RoundCorneredEditText.class);
    target.repeatCurrPass = Utils.findRequiredViewAsType(source, R.id.repeatCurrentPass, "field 'repeatCurrPass'", RoundCorneredEditText.class);
    target.profImage = Utils.findRequiredViewAsType(source, R.id.profPageImage, "field 'profImage'", CircleImageView.class);
    target.pinCodeSwitch = Utils.findRequiredViewAsType(source, R.id.switchPinCode, "field 'pinCodeSwitch'", Switch.class);
    target.v1 = Utils.findRequiredView(source, R.id.view, "field 'v1'");
    target.v3 = Utils.findRequiredView(source, R.id.view4, "field 'v3'");
    target.v4 = Utils.findRequiredView(source, R.id.view5, "field 'v4'");
  }

  @Override
  @CallSuper
  public void unbind() {
    SecurityFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.changePassword = null;
    target.back = null;
    target.erCurrent = null;
    target.erNew = null;
    target.erRepeat = null;
    target.deletePin = null;
    target.currPass = null;
    target.newCurrPass = null;
    target.repeatCurrPass = null;
    target.profImage = null;
    target.pinCodeSwitch = null;
    target.v1 = null;
    target.v3 = null;
    target.v4 = null;
  }
}
