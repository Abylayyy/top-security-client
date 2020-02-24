// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.domain.pincodeScreen;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.poovam.pinedittextfield.CirclePinField;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;

public class PinCodeActivity_ViewBinding implements Unbinder {
  private PinCodeActivity target;

  @UiThread
  public PinCodeActivity_ViewBinding(PinCodeActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public PinCodeActivity_ViewBinding(PinCodeActivity target, View source) {
    this.target = target;

    target.one = Utils.findRequiredViewAsType(source, R.id.oneBtn, "field 'one'", TextView.class);
    target.two = Utils.findRequiredViewAsType(source, R.id.twoBtn, "field 'two'", TextView.class);
    target.three = Utils.findRequiredViewAsType(source, R.id.threeBtn, "field 'three'", TextView.class);
    target.four = Utils.findRequiredViewAsType(source, R.id.fourBtn, "field 'four'", TextView.class);
    target.five = Utils.findRequiredViewAsType(source, R.id.fiveBtn, "field 'five'", TextView.class);
    target.six = Utils.findRequiredViewAsType(source, R.id.sixBtn, "field 'six'", TextView.class);
    target.seven = Utils.findRequiredViewAsType(source, R.id.sevenBtn, "field 'seven'", TextView.class);
    target.eight = Utils.findRequiredViewAsType(source, R.id.eightBtn, "field 'eight'", TextView.class);
    target.nine = Utils.findRequiredViewAsType(source, R.id.nineBtn, "field 'nine'", TextView.class);
    target.zero = Utils.findRequiredViewAsType(source, R.id.zeroBtn, "field 'zero'", TextView.class);
    target.faceID = Utils.findRequiredViewAsType(source, R.id.faceID, "field 'faceID'", ImageView.class);
    target.clearBtn = Utils.findRequiredViewAsType(source, R.id.clearBtn, "field 'clearBtn'", ImageView.class);
    target.pinEdit = Utils.findRequiredViewAsType(source, R.id.circleField, "field 'pinEdit'", CirclePinField.class);
    target.infoText = Utils.findRequiredViewAsType(source, R.id.infoText, "field 'infoText'", TextView.class);
    target.forgetPin = Utils.findRequiredViewAsType(source, R.id.forgetPin, "field 'forgetPin'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    PinCodeActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.one = null;
    target.two = null;
    target.three = null;
    target.four = null;
    target.five = null;
    target.six = null;
    target.seven = null;
    target.eight = null;
    target.nine = null;
    target.zero = null;
    target.faceID = null;
    target.clearBtn = null;
    target.pinEdit = null;
    target.infoText = null;
    target.forgetPin = null;
  }
}
