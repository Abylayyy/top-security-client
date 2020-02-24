// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.domain.PaymentScreen;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;

public class PaymentActivity_ViewBinding implements Unbinder {
  private PaymentActivity target;

  @UiThread
  public PaymentActivity_ViewBinding(PaymentActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public PaymentActivity_ViewBinding(PaymentActivity target, View source) {
    this.target = target;

    target.wv_payment_site = Utils.findRequiredViewAsType(source, R.id.wv_payment_site, "field 'wv_payment_site'", WebView.class);
    target.podpiska = Utils.findRequiredViewAsType(source, R.id.profileBottom, "field 'podpiska'", LinearLayout.class);
    target.main = Utils.findRequiredViewAsType(source, R.id.mainBottom, "field 'main'", LinearLayout.class);
    target.mainImg = Utils.findRequiredViewAsType(source, R.id.mainImage2, "field 'mainImg'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    PaymentActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.wv_payment_site = null;
    target.podpiska = null;
    target.main = null;
    target.mainImg = null;
  }
}
