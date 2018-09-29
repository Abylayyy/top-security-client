package kz.topsecurity.top_signal.presenter.registerPresenter;

import kz.topsecurity.top_signal.presenter.base.BasePresenter;

public interface RegisterPresenter extends BasePresenter {
    void register(String rawphone, String phone, String email, String userName, String password );
}
