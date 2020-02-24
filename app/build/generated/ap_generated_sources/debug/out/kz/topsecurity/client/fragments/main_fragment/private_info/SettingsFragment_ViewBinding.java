// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.fragments.main_fragment.private_info;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;

public class SettingsFragment_ViewBinding implements Unbinder {
  private SettingsFragment target;

  @UiThread
  public SettingsFragment_ViewBinding(SettingsFragment target, View source) {
    this.target = target;

    target.back = Utils.findRequiredViewAsType(source, R.id.backSecurity, "field 'back'", LinearLayout.class);
    target.profImage = Utils.findRequiredViewAsType(source, R.id.profPageImage, "field 'profImage'", CircleImageView.class);
    target.switchGps = Utils.findRequiredViewAsType(source, R.id.switchGPS, "field 'switchGps'", Switch.class);
    target.switchNot = Utils.findRequiredViewAsType(source, R.id.switchNotification, "field 'switchNot'", Switch.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SettingsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.back = null;
    target.profImage = null;
    target.switchGps = null;
    target.switchNot = null;
  }
}
