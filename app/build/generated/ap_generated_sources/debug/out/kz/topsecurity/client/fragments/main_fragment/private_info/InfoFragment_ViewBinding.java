// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.fragments.main_fragment.private_info;

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

public class InfoFragment_ViewBinding implements Unbinder {
  private InfoFragment target;

  @UiThread
  public InfoFragment_ViewBinding(InfoFragment target, View source) {
    this.target = target;

    target.back = Utils.findRequiredViewAsType(source, R.id.backSecurity, "field 'back'", LinearLayout.class);
    target.about = Utils.findRequiredViewAsType(source, R.id.aboutApp, "field 'about'", TextView.class);
    target.userConf = Utils.findRequiredViewAsType(source, R.id.userConf, "field 'userConf'", TextView.class);
    target.instruction = Utils.findRequiredViewAsType(source, R.id.instruction, "field 'instruction'", TextView.class);
    target.beginLearning = Utils.findRequiredViewAsType(source, R.id.beginLearning, "field 'beginLearning'", TextView.class);
    target.email = Utils.findRequiredViewAsType(source, R.id.emailSupport, "field 'email'", TextView.class);
    target.dial = Utils.findRequiredViewAsType(source, R.id.dialNum, "field 'dial'", TextView.class);
    target.profImage = Utils.findRequiredViewAsType(source, R.id.profPageImage, "field 'profImage'", CircleImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    InfoFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.back = null;
    target.about = null;
    target.userConf = null;
    target.instruction = null;
    target.beginLearning = null;
    target.email = null;
    target.dial = null;
    target.profImage = null;
  }
}
