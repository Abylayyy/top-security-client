package kz.topsecurity.client.di.components.AlertHistory;

import dagger.Component;
import dagger.Subcomponent;
import dagger.android.AndroidInjector;
import kz.topsecurity.client.di.modules.AlertHistory.AlertHistoryModule;
import kz.topsecurity.client.domain.AlertHistoryScreen.AlertHistoryActivity;

@Subcomponent(modules = {AlertHistoryModule.class})
public interface AlertHistoryComponent extends AndroidInjector<AlertHistoryActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<AlertHistoryActivity>{}
}
