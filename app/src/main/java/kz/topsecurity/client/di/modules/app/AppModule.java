package kz.topsecurity.client.di.modules.app;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kz.topsecurity.client.di.components.AlertHistory.AlertHistoryComponent;

@Module(subcomponents = {
        AlertHistoryComponent.class})
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

}