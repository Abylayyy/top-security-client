package kz.topsecurity.client.presenter.health_card_presenter;

public interface HealthCardPresenter {
    void saveHealthCardData(String blood, String data, String weight, String height, String allergy, String med, String zabo);
    void detach();
}
