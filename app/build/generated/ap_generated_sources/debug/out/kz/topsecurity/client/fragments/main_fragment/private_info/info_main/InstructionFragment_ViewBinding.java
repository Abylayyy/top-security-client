// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.fragments.main_fragment.private_info.info_main;

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

public class InstructionFragment_ViewBinding implements Unbinder {
  private InstructionFragment target;

  @UiThread
  public InstructionFragment_ViewBinding(InstructionFragment target, View source) {
    this.target = target;

    target.fullInstruction = Utils.findRequiredViewAsType(source, R.id.userConfTxt, "field 'fullInstruction'", TextView.class);
    target.back = Utils.findRequiredViewAsType(source, R.id.backSecurity, "field 'back'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    InstructionFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.fullInstruction = null;
    target.back = null;
  }
}
