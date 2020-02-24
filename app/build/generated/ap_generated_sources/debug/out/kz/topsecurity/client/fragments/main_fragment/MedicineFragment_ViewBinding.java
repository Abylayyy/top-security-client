// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.fragments.main_fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;

public class MedicineFragment_ViewBinding implements Unbinder {
  private MedicineFragment target;

  @UiThread
  public MedicineFragment_ViewBinding(MedicineFragment target, View source) {
    this.target = target;

    target.medDate = Utils.findRequiredViewAsType(source, R.id.med_date, "field 'medDate'", TextView.class);
    target.medGroup = Utils.findRequiredViewAsType(source, R.id.medGroup, "field 'medGroup'", TextView.class);
    target.medWeight = Utils.findRequiredViewAsType(source, R.id.medWeight, "field 'medWeight'", EditText.class);
    target.medHeight = Utils.findRequiredViewAsType(source, R.id.medHeight, "field 'medHeight'", EditText.class);
    target.medAll = Utils.findRequiredViewAsType(source, R.id.medAllergy, "field 'medAll'", EditText.class);
    target.medMedik = Utils.findRequiredViewAsType(source, R.id.medMedik, "field 'medMedik'", EditText.class);
    target.medZabo = Utils.findRequiredViewAsType(source, R.id.medZabo, "field 'medZabo'", EditText.class);
    target.addNewCon = Utils.findRequiredViewAsType(source, R.id.addNewCon, "field 'addNewCon'", TextView.class);
    target.medInfo = Utils.findRequiredViewAsType(source, R.id.changeMedInfo, "field 'medInfo'", TextView.class);
    target.profImage = Utils.findRequiredViewAsType(source, R.id.profPageImage, "field 'profImage'", CircleImageView.class);
    target.v1 = Utils.findRequiredView(source, R.id.view, "field 'v1'");
    target.v2 = Utils.findRequiredView(source, R.id.view2, "field 'v2'");
    target.v3 = Utils.findRequiredView(source, R.id.view3, "field 'v3'");
    target.v4 = Utils.findRequiredView(source, R.id.view4, "field 'v4'");
    target.v5 = Utils.findRequiredView(source, R.id.view5, "field 'v5'");
    target.v8 = Utils.findRequiredView(source, R.id.view8, "field 'v8'");
    target.v9 = Utils.findRequiredView(source, R.id.view9, "field 'v9'");
    target.v10 = Utils.findRequiredView(source, R.id.view10, "field 'v10'");
    target.phoneRecycler = Utils.findRequiredViewAsType(source, R.id.phoneRecycler, "field 'phoneRecycler'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MedicineFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.medDate = null;
    target.medGroup = null;
    target.medWeight = null;
    target.medHeight = null;
    target.medAll = null;
    target.medMedik = null;
    target.medZabo = null;
    target.addNewCon = null;
    target.medInfo = null;
    target.profImage = null;
    target.v1 = null;
    target.v2 = null;
    target.v3 = null;
    target.v4 = null;
    target.v5 = null;
    target.v8 = null;
    target.v9 = null;
    target.v10 = null;
    target.phoneRecycler = null;
  }
}
