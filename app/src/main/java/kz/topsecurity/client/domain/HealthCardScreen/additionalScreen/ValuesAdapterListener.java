package kz.topsecurity.client.domain.HealthCardScreen.additionalScreen;

interface ValuesAdapterListener {
    void onItemSelected(int index, String value);
    void onItemRemove(int index, String value);

    void showToastMsg(int already_added);
}
