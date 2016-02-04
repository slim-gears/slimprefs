// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs;

import com.slimgears.slimprefs.internal.AbstractClassBinding;
import com.slimgears.slimprefs.internal.ClassBinding;
import com.slimgears.slimprefs.internal.CompositePreferenceBinding;
import com.slimgears.slimprefs.internal.PreferenceObserver;

import java.util.Date;

/**
 * Created by ditskovi on 1/29/2016.
 *
 */
public class GeneratedDummyInjectionTargetClassBinding extends AbstractClassBinding<DummyInjectionTarget> {
    public static final ClassBinding<DummyInjectionTarget> INSTANCE = new GeneratedDummyInjectionTargetClassBinding();

    private GeneratedDummyInjectionTargetClassBinding() {}

    @Override
    public PreferenceBinding bind(PreferenceProvider provider, DummyInjectionTarget target) {
        return CompositePreferenceBinding.create(
                bindMember(provider.getPreference("DummyInjectionTarget.age", Integer.class), target.age, 0, new PreferenceObserver<Integer>() {
                    @Override
                    public void onChanged(Integer value) {
                            target.age = value;
                        }
                }),
                bindMember(provider.getPreference("DummyInjectionTarget.name", String.class), target.name, null, new PreferenceObserver<String>() {
                    @Override
                    public void onChanged(String value) {
                target.name = value;
            }
                }),
                bindMember(provider.getPreference("DummyInjectionTarget.installationDate", Date.class), target.installationDate, null, new PreferenceObserver<Date>() {
                    @Override
                    public void onChanged(Date value) {
                        target.installationDate = value;
                    }
                }),
                bindMemberTwoWay(provider.getPreference("DummyInjectionTarget.runCounter", Integer.class),
                                 new ValueProvider<Integer>() {
                                     @Override
                                     public Integer get() {
                                         return target.runCounter;
                                     }
                                 },
                                 0,
                                 new PreferenceObserver<Integer>() {
                                     @Override
                                     public void onChanged(Integer value) {
                                         target.runCounter = value;
                                     }
                                 }),
                bindMember(provider.getPreference("DummyInjectionTarget.registered", Boolean.class), target.registered, false, new PreferenceObserver<Boolean>() {
                    @Override
                    public void onChanged(Boolean value) {
                        target.onRegisteredChanged(value);
                    }
                }));
    }
}
