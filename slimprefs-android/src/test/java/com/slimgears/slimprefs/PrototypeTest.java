// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.slimgears.slimprefs.internal.ClassBinding;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ditskovi on 1/29/2016.
 *
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 16)
public class PrototypeTest {
    private SharedPreferences preferences;
    private PreferenceProvider preferenceProvider;
    private ClassBinding<DummyInjectionTarget> dummyClassBinding;

    @Before
    public void setUp() {
        preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        preferenceProvider = new SharedPreferenceProvider(RuntimeEnvironment.application);
        dummyClassBinding = GeneratedDummyInjectionTargetClassBinding.INSTANCE;
    }

    @Test
    public void onClassBinding_membersShouldBeInjected() throws ParseException {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("d/m/yyyy");
        preferences.edit()
                .putInt("DummyInjectionTarget.age", 20)
                .putString("DummyInjectionTarget.name", "John")
                .putLong("DummyInjectionTarget.installationDate", dateFormat.parse("1/1/2016").getTime())
                .putBoolean("DummyInjectionTarget.registered", true)
                .apply();

        DummyInjectionTarget dummyInjectionTarget = new DummyInjectionTarget();
        dummyClassBinding.bind(preferenceProvider, dummyInjectionTarget);
        Assert.assertEquals(20, dummyInjectionTarget.age);
        Assert.assertEquals("John", dummyInjectionTarget.name);
        Assert.assertEquals(dateFormat.parse("1/1/2016"), dummyInjectionTarget.installationDate);
        Assert.assertEquals(true, dummyInjectionTarget.registered);
    }

    @Test
    public void afterBinding_onPreferenceChange_memberGetsUpdated() throws ParseException {
        preferences.edit()
                .putInt("DummyInjectionTarget.age", 20)
                .apply();

        DummyInjectionTarget dummyInjectionTarget = new DummyInjectionTarget();
        PreferenceBinding binding = dummyClassBinding.bind(preferenceProvider, dummyInjectionTarget);
        Assert.assertEquals(20, dummyInjectionTarget.age);

        preferences.edit()
                .putInt("DummyInjectionTarget.age", 25)
                .apply();
        Assert.assertEquals(25, dummyInjectionTarget.age);

        binding.unbind();

        preferences.edit()
                .putInt("DummyInjectionTarget.age", 30)
                .apply();
        Assert.assertEquals(25, dummyInjectionTarget.age);
    }

    @Test
    public void whenNoPreferenceExists_and_DefaultValueIsDefined_defaultIsWrittenToPreferences() {
        DummyInjectionTarget dummyInjectionTarget = new DummyInjectionTarget();
        PreferenceBinding binding = dummyClassBinding.bind(preferenceProvider, dummyInjectionTarget);
        Assert.assertEquals(15, dummyInjectionTarget.age);
        Assert.assertEquals(15, preferences.getInt("DummyInjectionTarget.age", 0));
        Assert.assertFalse(preferences.contains("DummyInjectionTarget.name"));

        binding.unbind();
    }
}
