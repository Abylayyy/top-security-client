// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.domain.PlaceScreen;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;

public class PlaceActivity_ViewBinding implements Unbinder {
  private PlaceActivity target;

  @UiThread
  public PlaceActivity_ViewBinding(PlaceActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public PlaceActivity_ViewBinding(PlaceActivity target, View source) {
    this.target = target;

    target.back = Utils.findRequiredViewAsType(source, R.id.backLinear, "field 'back'", LinearLayout.class);
    target.addPlaceIcon = Utils.findRequiredViewAsType(source, R.id.addMapIcon, "field 'addPlaceIcon'", FloatingActionButton.class);
    target.favorIcon = Utils.findRequiredViewAsType(source, R.id.favorIcon, "field 'favorIcon'", FloatingActionButton.class);
    target.gpsIcon = Utils.findRequiredViewAsType(source, R.id.gpsIcon, "field 'gpsIcon'", FloatingActionButton.class);
    target.searchTxt = Utils.findRequiredViewAsType(source, R.id.searchTxt, "field 'searchTxt'", AutoCompleteTextView.class);
    target.searchIcon = Utils.findRequiredViewAsType(source, R.id.searchIcon, "field 'searchIcon'", CardView.class);
    target.addFavorBottom = Utils.findRequiredViewAsType(source, R.id.addFavorBottomSheet, "field 'addFavorBottom'", ConstraintLayout.class);
    target.placeName = Utils.findRequiredViewAsType(source, R.id.nameOfPlace, "field 'placeName'", EditText.class);
    target.favorError = Utils.findRequiredView(source, R.id.favorNameView, "field 'favorError'");
    target.savePlace = Utils.findRequiredViewAsType(source, R.id.savePlace, "field 'savePlace'", TextView.class);
    target.favorListBottom = Utils.findRequiredViewAsType(source, R.id.favorListBottomSheet, "field 'favorListBottom'", ConstraintLayout.class);
    target.favorRecycler = Utils.findRequiredViewAsType(source, R.id.favorRecycler, "field 'favorRecycler'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    PlaceActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.back = null;
    target.addPlaceIcon = null;
    target.favorIcon = null;
    target.gpsIcon = null;
    target.searchTxt = null;
    target.searchIcon = null;
    target.addFavorBottom = null;
    target.placeName = null;
    target.favorError = null;
    target.savePlace = null;
    target.favorListBottom = null;
    target.favorRecycler = null;
  }
}
