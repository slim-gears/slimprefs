// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.internal;

import com.slimgears.slimprefs.PreferenceInjector;
import com.slimgears.slimprefs.PreferenceInjectorFactory;
import com.slimgears.slimprefs.PreferenceProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ditskovi on 2/4/2016.
 *
 */
public class AbstractPreferenceInjectorFactory implements PreferenceInjectorFactory {
    private final Map<Class, ClassBinding> classBindings = new HashMap<>();

    @Override
    public PreferenceInjector createInjector(PreferenceProvider preferenceProvider) {
        return new DefaultPreferenceInjector(preferenceProvider, classBindings);
    }

    protected <T> void addBinding(Class<T> targetClass, ClassBinding<T> binding) {
        classBindings.put(targetClass, binding);
    }
}
