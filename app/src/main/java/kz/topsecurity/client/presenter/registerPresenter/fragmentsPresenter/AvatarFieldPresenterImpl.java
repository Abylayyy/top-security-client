package kz.topsecurity.client.presenter.registerPresenter.fragmentsPresenter;

import android.graphics.Bitmap;

import kz.topsecurity.client.R;
import kz.topsecurity.client.view.registerView.fragmentsView.AvatarFieldView;

public class AvatarFieldPresenterImpl implements AvatarFieldPresenter{
    AvatarFieldView view;

    public AvatarFieldPresenterImpl(AvatarFieldView view){
        this.view = view;
    }

    @Override
    public void checkAvatar(Bitmap bitmap) {
    }

    @Override
    public void detach() {

    }
}
