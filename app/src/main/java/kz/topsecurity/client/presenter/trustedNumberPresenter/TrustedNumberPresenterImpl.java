package kz.topsecurity.client.presenter.trustedNumberPresenter;

import java.util.ArrayList;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kz.topsecurity.client.R;
import kz.topsecurity.client.helper.Constants;
import kz.topsecurity.client.model.auth.AuthResponse;
import kz.topsecurity.client.model.contact.Contact;
import kz.topsecurity.client.model.contact.GetContactsResponse;
import kz.topsecurity.client.model.contact.SaveContactsResponse;
import kz.topsecurity.client.model.other.BasicResponse;
import kz.topsecurity.client.presenter.base.BasePresenterImpl;
import kz.topsecurity.client.service.api.RequestService;
import kz.topsecurity.client.service.api.RetrofitClient;
import kz.topsecurity.client.view.base.BaseView;
import kz.topsecurity.client.view.trustedNumberView.TrustedNumbersView;

public class TrustedNumberPresenterImpl extends BasePresenterImpl<TrustedNumbersView> implements TrustedNumberPresenter{
    public TrustedNumberPresenterImpl(TrustedNumbersView view) {
        super(view);
    }

    @Override
    public void getTrustedContacts() {
        view.showLoadingDialog();

        Disposable success = new RequestService<>(new RequestService.RequestResponse<GetContactsResponse>() {
            @Override
            public void onSuccess(GetContactsResponse r) {
               if(r.getContacts()==null){
                   view.hideLoadingDialog();
                   view.onListLoaded(new ArrayList<>());
               }
               else{
                   view.hideLoadingDialog();
                   view.onListLoaded(r.getContacts());
               }
            }

            @Override
            public void onFailed(GetContactsResponse data, int error_message) {
                setGetContactsError(error_message);
            }

            @Override
            public void onError(Throwable e, int error_message) {
                setGetContactsError(error_message);
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .getContacts(RetrofitClient.getRequestToken()));

        compositeDisposable.add(success);
    }

    @Override
    public void deleteContact(Integer id) {
        view.showLoadingDialog();
        Disposable subscribe = new RequestService<>(new RequestService.RequestResponse<BasicResponse>() {
            @Override
            public void onSuccess(BasicResponse r) {
                setDeleteContactSuccess(id);
            }

            @Override
            public void onFailed(BasicResponse data, int error_message) {
                setDeleteContactError();
            }

            @Override
            public void onError(Throwable e, int error_message) {
                setDeleteContactError();
            }
        }).makeRequest(RetrofitClient.getClientApi()
                .deleteContact(RetrofitClient.getRequestToken(), id));
        compositeDisposable.add(subscribe);
    }

    private void setDeleteContactError() {
        view.hideLoadingDialog();
        view.onShowToast("Не удалось удалить");
    }

    private void setDeleteContactSuccess(int id) {
        view.hideLoadingDialog();
        view.onDeleteSuccess(id);
    }

    private void setGetContactsError(int error_message) {
        view.hideLoadingDialog();
        view.onContactLoadError(error_message);
    }

    @Override
    public void detach() {
        compositeDisposable.clear();
        super.detach();
    }
}
