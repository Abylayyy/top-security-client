// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.domain.RegisterScreen;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;

public class InfoActivity_ViewBinding implements Unbinder {
  private InfoActivity target;

  @UiThread
  public InfoActivity_ViewBinding(InfoActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public InfoActivity_ViewBinding(InfoActivity target, View source) {
    this.target = target;

    target.fullTxt = Utils.findRequiredViewAsType(source, R.id.userConfTxt, "field 'fullTxt'", TextView.class);
    target.back = Utils.findRequiredViewAsType(source, R.id.backSecurity, "field 'back'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    InfoActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.fullTxt = null;
    target.back = null;
  }
}
