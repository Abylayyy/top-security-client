package kz.topsecurity.client.di.components.app;

import android.app.Application;

import com.theartofdev.edmodo.cropper.CropImage;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import kz.topsecurity.client.application.TopSecurityClientApplication;
import kz.topsecurity.client.di.modules.app.ActivityBuilder;
import kz.topsecurity.client.di.modules.app.AppModule;

@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        ActivityBuilder.class})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }

    void inject(TopSecurityClientApplication app);
}
