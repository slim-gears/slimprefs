// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs;

import com.slimgears.slimprefs.internal.ClassBinding;
import com.slimgears.slimprefs.internal.CompositePreferenceBinding;
import com.slimgears.slimprefs.internal.PreferenceObserver;

import java.util.Date;

/**
 * Created by ditskovi on 1/29/2016.
 *
 */
public class GeneratedDummyInjectionTargetClassBinding implements ClassBinding<DummyInjectionTarget> {
    public static final ClassBinding<DummyInjectionTarget> INSTANCE = new GeneratedDummyInjectionTargetClassBinding();

    private GeneratedDummyInjectionTargetClassBinding() {}

    @Override
    public PreferenceBinding bind(PreferenceProvider provider, DummyInjectionTarget target) {
        return CompositePreferenceBinding.create(
                provider.getPreference("DummyInjectionTarget.age", Integer.class).observe(new PreferenceObserver<Integer>() {
                    @Override
                    public void onChanged(Integer value) {
                        target.age = value;
                    }
                }),
                provider.getPreference("DummyInjectionTarget.name", String.class).observe(new PreferenceObserver<String>() {
                    @Override
                    public void onChanged(String value) {
                        target.name = value;
                    }
                }),
                provider.getPreference("DummyInjectionTarget.installationDate", Date.class).observe(new PreferenceObserver<Date>() {
                    @Override
                    public void onChanged(Date value) {
                        target.installationDate = value;
                    }
                }),
                provider.getPreference("DummyInjectionTarget.registered", Boolean.class).observe(new PreferenceObserver<Boolean>() {
                    @Override
                    public void onChanged(Boolean value) {
                        target.onRegisteredChanged(value);
                    }
                }));
    }
}
