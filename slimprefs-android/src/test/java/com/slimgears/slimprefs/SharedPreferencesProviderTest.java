// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.slimgears.slimprefs.internal.PreferenceObserver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ditskovi on 1/30/2016.
 *
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 16, constants = BuildConfig.class)
public class SharedPreferencesProviderTest {
    private PreferenceProvider provider;
    private SharedPreferences preferences;

    interface Validator<T> {
        void assertEquals(T expected, T actual);
    }

    interface CustomSerializable extends Serializable {
        String getFullName();
    }

    static class CustomSerializableImplementation implements CustomSerializable {
        String firstName;
        String lastName;

        CustomSerializableImplementation(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        public String getFullName() {
            return firstName + " " + lastName;
        }
    }

    class MockObserver<T> implements PreferenceObserver<T> {
        private final List<T> values = new ArrayList<>();

        @Override
        public void onChanged(T value) {
            values.add(value);
        }

        public void assertNotEmpty() {
            Assert.assertTrue(!values.isEmpty());
        }

        public T first() {
            assertNotEmpty();
            return values.get(0);
        }

        public void assertCount(int count) {
            Assert.assertEquals(count, values.size());
        }

        public void assertEmpty() {
            Assert.assertTrue(values.isEmpty());
        }
    }

    @Before
    public void setUp() {
        provider = new SharedPreferenceProvider(RuntimeEnvironment.application);
        preferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
    }

    @Test
    public void getPreference_returnsCachedValue() {
        PreferenceValue<String> pref = provider.getPreference("test", String.class);
        Assert.assertSame(pref, provider.getPreference("test", String.class));
    }

    @Test
    public void setValue_writtenToSharedPreferences() {
        provider.getPreference("test", String.class).set("set-test-value");
        Assert.assertEquals("set-test-value", preferences.getString("test", null));
    }

    @Test
    public void getValue_readFromSharedPreferences() {
        preferences.edit().putString("test", "get-test-value").apply();
        Assert.assertEquals("get-test-value", provider.getPreference("test", String.class).get());
    }

    @Test
    public void observeValue_observerIsCalledOnPreferenceChange() {
        MockObserver<String> mockObserver = new MockObserver<>();
        provider.getPreference("test", String.class).observe(mockObserver);
        mockObserver.assertEmpty();

        preferences.edit().putString("test", "observer-test").apply();
        mockObserver.assertNotEmpty();
        mockObserver.assertCount(1);
        Assert.assertEquals("observer-test", mockObserver.first());
    }

    @Test
    public void observeValue_whenPreferenceExists_observerIsCalledOnBinding() {
        preferences.edit().putString("test", "observer-test").apply();

        MockObserver<String> mockObserver = new MockObserver<>();
        provider.getPreference("test", String.class).observe(mockObserver);
        mockObserver.assertNotEmpty();
        mockObserver.assertCount(1);
        Assert.assertEquals("observer-test", mockObserver.first());
    }

    @Test
    public void setValue_thenGetValue_shouldEqual() {
        testSetGetForType(Integer.class, 1001);
        testSetGetForType(Long.class, 100001L);
        testSetGetForType(Date.class, new Date(1000000));
        testSetGetForType(Float.class, 0.1F);
        testSetGetForType(Boolean.class, true);
        testSetGetForType(String.class, "set-get-test");
        testSetGetForType(Double.class, 0.123456789012345678012345670123456012345);
        testSetGetForType(byte[].class, new byte[] { 0,1,2,3,4,5 }, Assert::assertArrayEquals);
    }

    @Test
    public void setParcelable_theGetParcelable_shouldEqual() {
        String key = "test-" + Bundle.class.getSimpleName();
        PreferenceValue<Bundle> preferenceValue = provider.getPreference(key, Bundle.class);
        Bundle bundle = new Bundle();
        bundle.putLong("test-item", 333);
        preferenceValue.set(bundle);
        Bundle restoredBundle = preferenceValue.get();
        Assert.assertNotNull(restoredBundle);
        Assert.assertNotSame(bundle, restoredBundle);
        Assert.assertEquals(333, restoredBundle.getLong("test-item"));
    }

    @Test
    public void getSerializablePreference_shouldReturnPreference() {
        PreferenceValue<CustomSerializable> serializablePreferenceValue = provider.getPreference("test-custom-serializable", CustomSerializable.class);
        Assert.assertNotNull(serializablePreferenceValue);
        serializablePreferenceValue.set(new CustomSerializableImplementation("John", "Doe"));

        PreferenceProvider newProvider = new SharedPreferenceProvider(RuntimeEnvironment.application);
        serializablePreferenceValue = newProvider.getPreference("test-custom-serializable", CustomSerializable.class);
        CustomSerializable value = serializablePreferenceValue.get();
        Assert.assertNotNull(value);

        Assert.assertEquals("John Doe", value.getFullName());
    }

    private <T> void testSetGetForType(Class<T> valueType, T value) {
        testSetGetForType(valueType, value, Assert::assertEquals);
    }

    private <T> void testSetGetForType(Class<T> valueType, T value, Validator<T> validator) {
        String key = "test-" + valueType.getSimpleName();
        PreferenceValue<T> preferenceValue = provider.getPreference(key, valueType);
        Assert.assertNotNull(preferenceValue);

        preferenceValue.set(value);
        T restoredValue = preferenceValue.get();
        validator.assertEquals(value, restoredValue);
    }
}
