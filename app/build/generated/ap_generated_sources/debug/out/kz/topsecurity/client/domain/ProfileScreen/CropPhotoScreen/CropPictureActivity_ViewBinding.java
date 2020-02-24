// Generated code from Butter Knife. Do not modify!
package kz.topsecurity.client.domain.ProfileScreen.CropPhotoScreen;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.lang.IllegalStateException;
import java.lang.Override;
import kz.topsecurity.client.R;

public class CropPictureActivity_ViewBinding implements Unbinder {
  private CropPictureActivity target;

  @UiThread
  public CropPictureActivity_ViewBinding(CropPictureActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public CropPictureActivity_ViewBinding(CropPictureActivity target, View source) {
    this.target = target;

    target.cropView = Utils.findRequiredViewAsType(source, R.id.crop_view, "field 'cropView'", CropImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    CropPictureActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.cropView = null;
  }
}
