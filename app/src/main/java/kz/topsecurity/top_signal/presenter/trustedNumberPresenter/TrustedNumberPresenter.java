package kz.topsecurity.top_signal.presenter.trustedNumberPresenter;

import kz.topsecurity.top_signal.presenter.base.BasePresenter;

public interface TrustedNumberPresenter extends BasePresenter {
    void getTrustedContacts();
    void deleteContact(Integer id);
}
