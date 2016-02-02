package com.slimgears.slimprefs.example;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;

import java.util.Date;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 16, constants = BuildConfig.class)
public class ApplicationTest {
    private ActivityController<MainActivity> mainActivityController;
    private App app;

    @Before
    public void setUp() {
        Assert.assertTrue(RuntimeEnvironment.application instanceof App);
        app = (App)RuntimeEnvironment.application;
        mainActivityController = Robolectric.buildActivity(MainActivity.class).create();
    }

    @Test
    public void testActivityLifeCycle() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        preferences
                .edit()
                .putString("MainActivity.stringPreferenceDefaultKey", "TestStringValue")
                .putInt("int_preference_explicit_key", 22034)
                .putLong(RuntimeEnvironment.application.getString(R.string.date_preference_key_res), 12090909033456L)
                .apply();

        MainActivity activity = mainActivityController
                .start()
                .resume()
                .visible()
                .get();

        Assert.assertEquals("TestStringValue", activity.stringPreferenceDefaultKey);
        Assert.assertEquals(22034, activity.intPreferenceExplicitKey);
        Assert.assertEquals(new Date(12090909033456L), activity.datePreferenceKeyRes);

        preferences
                .edit()
                .putString("MainActivity.stringPreferenceDefaultKey", "NewStringValue")
                .apply();

        Assert.assertEquals("NewStringValue", activity.stringPreferenceDefaultKey);

        mainActivityController
                .pause()
                .stop()
                .destroy();
    }
}
