package kz.topsecurity.client.presenter.trustedNumberPresenter;

import kz.topsecurity.client.presenter.base.BasePresenter;

public interface TrustedNumberPresenter extends BasePresenter {
    void getTrustedContacts();
    void deleteContact(Integer id);
}
