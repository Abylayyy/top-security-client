// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.fragments.main_fragment.private_info.info_main;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;

public class AboutAppFragment_ViewBinding implements Unbinder {
  private AboutAppFragment target;

  @UiThread
  public AboutAppFragment_ViewBinding(AboutAppFragment target, View source) {
    this.target = target;

    target.back = Utils.findRequiredViewAsType(source, R.id.backSecurity, "field 'back'", LinearLayout.class);
    target.version = Utils.findRequiredViewAsType(source, R.id.versiontxt, "field 'version'", TextView.class);
    target.profImage = Utils.findRequiredViewAsType(source, R.id.profPageImage, "field 'profImage'", CircleImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AboutAppFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.back = null;
    target.version = null;
    target.profImage = null;
  }
}
