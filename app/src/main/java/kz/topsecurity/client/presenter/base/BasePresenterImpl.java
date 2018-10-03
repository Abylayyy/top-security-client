package kz.topsecurity.client.presenter.base;

import io.reactivex.disposables.CompositeDisposable;
import kz.topsecurity.client.view.base.BaseView;

public abstract class BasePresenterImpl<V extends BaseView> implements BasePresenter {

    protected V view;
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    public BasePresenterImpl(V view){
        this.view = view;
    }

    @Override
    public void attach() {

    }

    @Override
    public void detach() {
        compositeDisposable.clear();
    }
}
