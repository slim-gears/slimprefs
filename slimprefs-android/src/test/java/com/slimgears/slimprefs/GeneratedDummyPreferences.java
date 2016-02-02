// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs;

import java.util.Date;

/**
 * Created by ditskovi on 1/29/2016.
 *
 */
public class GeneratedDummyPreferences implements DummyPreferences {
    private final PreferenceValue<Integer> agePreference;
    private final PreferenceValue<String> namePreference;
    private final PreferenceValue<Date> installationDatePreference;
    private final PreferenceValue<Boolean> registeredPreference;

    public GeneratedDummyPreferences(PreferenceProvider provider) {
        agePreference = provider.getPreference("DummyPreferences.age", Integer.class);
        namePreference = provider.getPreference("DummyPreferences.name", String.class);
        installationDatePreference = provider.getPreference("DummyPreferences.installationDate", Date.class);
        registeredPreference = provider.getPreference("DummyPreferences.registered", Boolean.class);
    }

    @Override
    public int getAge() {
        return agePreference.get();
    }

    @Override
    public void setAge(int age) {
        agePreference.set(age);
    }

    @Override
    public String getName() {
        return namePreference.get();
    }

    @Override
    public void setName(String name) {
        namePreference.set(name);
    }

    @Override
    public Date getInstallationDate() {
        return installationDatePreference.get();
    }

    @Override
    public void setInstallationDate(Date date) {
        installationDatePreference.set(date);
    }

    @Override
    public boolean isRegistered() {
        return registeredPreference.get();
    }

    @Override
    public void setRegistered(boolean registered) {
        registeredPreference.set(registered);
    }
}
