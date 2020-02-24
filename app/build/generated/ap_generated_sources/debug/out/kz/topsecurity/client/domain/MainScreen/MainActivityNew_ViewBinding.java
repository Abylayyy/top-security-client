// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.domain.MainScreen;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;

public class MainActivityNew_ViewBinding implements Unbinder {
  private MainActivityNew target;

  @UiThread
  public MainActivityNew_ViewBinding(MainActivityNew target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivityNew_ViewBinding(MainActivityNew target, View source) {
    this.target = target;

    target.navProf = Utils.findRequiredViewAsType(source, R.id.profNav, "field 'navProf'", ImageView.class);
    target.navMed = Utils.findRequiredViewAsType(source, R.id.medNav, "field 'navMed'", ImageView.class);
    target.med = Utils.findRequiredViewAsType(source, R.id.medBottom, "field 'med'", LinearLayout.class);
    target.main = Utils.findRequiredViewAsType(source, R.id.mainImage, "field 'main'", ImageView.class);
    target.bottom = Utils.findRequiredViewAsType(source, R.id.mainBottom, "field 'bottom'", LinearLayout.class);
    target.prof = Utils.findRequiredViewAsType(source, R.id.profileBottom, "field 'prof'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MainActivityNew target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.navProf = null;
    target.navMed = null;
    target.med = null;
    target.main = null;
    target.bottom = null;
    target.prof = null;
  }
}
