package com.slimgears.slimprefs.example;

import android.app.Application;

import com.slimgears.slimprefs.GeneratePreferenceInjector;
import com.slimgears.slimprefs.PreferenceInjector;
import com.slimgears.slimprefs.PreferenceProvider;
import com.slimgears.slimprefs.SharedPreferenceProvider;

/**
 * Created by Denis on 26/09/2015.
 *
 */
public class App extends Application {
    @GeneratePreferenceInjector
    interface AppPreferenceInjector extends PreferenceInjector {}

    private PreferenceInjector preferenceInjector;

    public PreferenceInjector getPreferenceInjector() {
        return preferenceInjector;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceProvider preferenceProvider = new SharedPreferenceProvider(this);
        preferenceInjector = new GeneratedApp_AppPreferenceInjector(preferenceProvider);
    }
}
