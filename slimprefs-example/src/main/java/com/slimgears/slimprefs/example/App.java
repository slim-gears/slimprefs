package com.slimgears.slimprefs.example;

import android.app.Application;

import com.slimgears.slimprefs.PreferenceFactory;
import com.slimgears.slimprefs.PreferenceInjector;
import com.slimgears.slimprefs.PreferenceInjectorFactory;
import com.slimgears.slimprefs.PreferenceProvider;
import com.slimgears.slimprefs.SharedPreferenceProvider;

/**
 * Created by Denis on 26/09/2015.
 *
 */
public class App extends Application {
    @PreferenceFactory
    interface AppPreferenceInjectorFactory extends PreferenceInjectorFactory {}

    private PreferenceInjector preferenceInjector;

    public PreferenceInjector getPreferenceInjector() {
        return preferenceInjector;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceProvider preferenceProvider = new SharedPreferenceProvider(this);
        preferenceInjector = GeneratedApp_AppPreferenceInjectorFactory.INSTANCE.createInjector(preferenceProvider);
    }
}
