// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.domain.AlertHistoryScreen;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;

public class AlertHistoryActivity_ViewBinding implements Unbinder {
  private AlertHistoryActivity target;

  @UiThread
  public AlertHistoryActivity_ViewBinding(AlertHistoryActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public AlertHistoryActivity_ViewBinding(AlertHistoryActivity target, View source) {
    this.target = target;

    target.rv_alerts = Utils.findRequiredViewAsType(source, R.id.rv_alerts, "field 'rv_alerts'", RecyclerView.class);
    target.tv_empty_list = Utils.findRequiredViewAsType(source, R.id.tv_empty_list, "field 'tv_empty_list'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AlertHistoryActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.rv_alerts = null;
    target.tv_empty_list = null;
  }
}
