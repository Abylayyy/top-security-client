package kz.topsecurity.client.di.modules.AlertHistory;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import kz.topsecurity.client.di.scopes.ActivityScope;
import kz.topsecurity.client.domain.AlertHistoryScreen.AlertHistoryActivity;
import kz.topsecurity.client.domain.AlertHistoryScreen.adapter.AlertListAdapter;
import kz.topsecurity.client.model.alertList.Alert;
import kz.topsecurity.client.view.alertHistoryView.AlertHistoryView;

@Module
public class AlertHistoryModule {
    @Provides
    AlertListAdapter provideAlertHistoryActivity(AlertHistoryActivity alertHistoryActivity) {
        return new AlertListAdapter(alertHistoryActivity, new ArrayList<>(), alertHistoryActivity);
    }

    @Provides
    AlertHistoryView provideAlertHistoryView(AlertHistoryActivity alertHistoryActivity) {
        return alertHistoryActivity;
    }

    @Provides
    AlertListAdapter.PlaceListAdapterListener providePlaceListAdapterListener(AlertHistoryActivity alertHistoryActivity) {
        return alertHistoryActivity;
    }
}
