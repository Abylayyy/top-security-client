package kz.topsecurity.client.di.modules.app;

import android.app.Activity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;
import kz.topsecurity.client.di.components.AlertHistory.AlertHistoryComponent;
import kz.topsecurity.client.domain.AlertHistoryScreen.AlertHistoryActivity;

@Module
public abstract class ActivityBuilder {

    @Binds
    @IntoMap
    @ActivityKey(AlertHistoryActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindMainActivity(AlertHistoryComponent.Builder builder);


}