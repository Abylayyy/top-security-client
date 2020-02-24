// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.domain.MainScreen;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;

public class AlertActivity_ViewBinding implements Unbinder {
  private AlertActivity target;

  @UiThread
  public AlertActivity_ViewBinding(AlertActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public AlertActivity_ViewBinding(AlertActivity target, View source) {
    this.target = target;

    target.topLayout = Utils.findRequiredViewAsType(source, R.id.topConstLayout, "field 'topLayout'", ConstraintLayout.class);
    target.bottomLayout = Utils.findRequiredViewAsType(source, R.id.bottomConstLayout, "field 'bottomLayout'", ConstraintLayout.class);
    target.alertBtn = Utils.findRequiredViewAsType(source, R.id.alertBtn, "field 'alertBtn'", ImageView.class);
    target.profImage = Utils.findRequiredViewAsType(source, R.id.profImage, "field 'profImage'", RelativeLayout.class);
    target.pokupkaImg = Utils.findRequiredViewAsType(source, R.id.pokupkaImage, "field 'pokupkaImg'", RelativeLayout.class);
    target.callPopup = Utils.findRequiredViewAsType(source, R.id.callPopup, "field 'callPopup'", RelativeLayout.class);
    target.skoryiPopup = Utils.findRequiredViewAsType(source, R.id.skoryiPopup, "field 'skoryiPopup'", RelativeLayout.class);
    target.cancelAlert = Utils.findRequiredViewAsType(source, R.id.cancelAlert, "field 'cancelAlert'", ImageView.class);
    target.rect5 = Utils.findRequiredViewAsType(source, R.id.animRect, "field 'rect5'", ImageView.class);
    target.redBtn = Utils.findRequiredViewAsType(source, R.id.redAlertBtn, "field 'redBtn'", ImageView.class);
    target.backGr = Utils.findRequiredViewAsType(source, R.id.background_image, "field 'backGr'", ImageView.class);
    target.krugPriniat = Utils.findRequiredViewAsType(source, R.id.krugPriniat, "field 'krugPriniat'", ImageView.class);
    target.krugVputi = Utils.findRequiredViewAsType(source, R.id.krugVPuti, "field 'krugVputi'", ImageView.class);
    target.krugUspeh = Utils.findRequiredViewAsType(source, R.id.krugUspeh, "field 'krugUspeh'", ImageView.class);
    target.imgPriniat = Utils.findRequiredViewAsType(source, R.id.priniatImage, "field 'imgPriniat'", ImageView.class);
    target.imgEdet = Utils.findRequiredViewAsType(source, R.id.rrt_edet, "field 'imgEdet'", ImageView.class);
    target.imgSuccess = Utils.findRequiredViewAsType(source, R.id.success, "field 'imgSuccess'", ImageView.class);
    target.rectSuccess = Utils.findRequiredViewAsType(source, R.id.successImage, "field 'rectSuccess'", ImageView.class);
    target.rectAccept = Utils.findRequiredViewAsType(source, R.id.acceptedImg, "field 'rectAccept'", ImageView.class);
    target.rectVputi = Utils.findRequiredViewAsType(source, R.id.mgorVputi, "field 'rectVputi'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AlertActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.topLayout = null;
    target.bottomLayout = null;
    target.alertBtn = null;
    target.profImage = null;
    target.pokupkaImg = null;
    target.callPopup = null;
    target.skoryiPopup = null;
    target.cancelAlert = null;
    target.rect5 = null;
    target.redBtn = null;
    target.backGr = null;
    target.krugPriniat = null;
    target.krugVputi = null;
    target.krugUspeh = null;
    target.imgPriniat = null;
    target.imgEdet = null;
    target.imgSuccess = null;
    target.rectSuccess = null;
    target.rectAccept = null;
    target.rectVputi = null;
  }
}
