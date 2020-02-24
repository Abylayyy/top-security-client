// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.fragments.main_fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;
import kz.topsecurity.client.ui_widgets.roundCorneredEditText.RoundCorneredEditTextWithPhoneMask;

public class ProfileMainFragment_ViewBinding implements Unbinder {
  private ProfileMainFragment target;

  @UiThread
  public ProfileMainFragment_ViewBinding(ProfileMainFragment target, View source) {
    this.target = target;

    target.profImage = Utils.findRequiredViewAsType(source, R.id.profPageImage, "field 'profImage'", CircleImageView.class);
    target.changeInfo = Utils.findRequiredViewAsType(source, R.id.changeProfInfo, "field 'changeInfo'", TextView.class);
    target.profName = Utils.findRequiredViewAsType(source, R.id.profName, "field 'profName'", EditText.class);
    target.surName = Utils.findRequiredViewAsType(source, R.id.profSurnameTxt, "field 'surName'", EditText.class);
    target.profZhsn = Utils.findRequiredViewAsType(source, R.id.profIyn, "field 'profZhsn'", EditText.class);
    target.profPhone = Utils.findRequiredViewAsType(source, R.id.profPhoneNum, "field 'profPhone'", RoundCorneredEditTextWithPhoneMask.class);
    target.profEmail = Utils.findRequiredViewAsType(source, R.id.profEmail, "field 'profEmail'", EditText.class);
    target.v1 = Utils.findRequiredView(source, R.id.view, "field 'v1'");
    target.v3 = Utils.findRequiredView(source, R.id.view3, "field 'v3'");
    target.v4 = Utils.findRequiredView(source, R.id.view4, "field 'v4'");
    target.v5 = Utils.findRequiredView(source, R.id.view5, "field 'v5'");
    target.errName = Utils.findRequiredViewAsType(source, R.id.errName, "field 'errName'", TextView.class);
    target.errSurname = Utils.findRequiredViewAsType(source, R.id.errSurname, "field 'errSurname'", TextView.class);
    target.errIyn = Utils.findRequiredViewAsType(source, R.id.errIyn, "field 'errIyn'", TextView.class);
    target.errEmail = Utils.findRequiredViewAsType(source, R.id.errEmail, "field 'errEmail'", TextView.class);
    target.profFavor = Utils.findRequiredViewAsType(source, R.id.profFavor, "field 'profFavor'", TextView.class);
    target.profSecure = Utils.findRequiredViewAsType(source, R.id.profSecur, "field 'profSecure'", TextView.class);
    target.profSetting = Utils.findRequiredViewAsType(source, R.id.profSetting, "field 'profSetting'", TextView.class);
    target.profInf = Utils.findRequiredViewAsType(source, R.id.profInf, "field 'profInf'", TextView.class);
    target.profLogout = Utils.findRequiredViewAsType(source, R.id.profLogout, "field 'profLogout'", TextView.class);
    target.errImage = Utils.findRequiredViewAsType(source, R.id.errImage, "field 'errImage'", ImageView.class);
    target.profPlace = Utils.findRequiredViewAsType(source, R.id.placeProf, "field 'profPlace'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ProfileMainFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.profImage = null;
    target.changeInfo = null;
    target.profName = null;
    target.surName = null;
    target.profZhsn = null;
    target.profPhone = null;
    target.profEmail = null;
    target.v1 = null;
    target.v3 = null;
    target.v4 = null;
    target.v5 = null;
    target.errName = null;
    target.errSurname = null;
    target.errIyn = null;
    target.errEmail = null;
    target.profFavor = null;
    target.profSecure = null;
    target.profSetting = null;
    target.profInf = null;
    target.profLogout = null;
    target.errImage = null;
    target.profPlace = null;
  }
}
