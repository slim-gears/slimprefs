// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.internal;

import com.slimgears.slimprefs.PreferenceBinding;
import com.slimgears.slimprefs.PreferenceInjector;
import com.slimgears.slimprefs.PreferenceProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ditskovi on 1/31/2016.
 *
 */
class DefaultPreferenceInjector implements PreferenceInjector {
    private final PreferenceProvider preferenceProvider;
    private final Map<Class, ClassBinding> classBindings;

    DefaultPreferenceInjector(PreferenceProvider preferenceProvider, Map<Class, ClassBinding> classBindings) {
        this.classBindings = classBindings;
        this.preferenceProvider = preferenceProvider;
    }

    @Override
    public <T> PreferenceBinding bind(T target) {
        //noinspection unchecked
        return getBinding((Class<T>)target.getClass()).bind(preferenceProvider, target);
    }

    private <T> ClassBinding<? super T> getBinding(Class<T> targetClass) {
        Class cls = targetClass;

        while (cls != Object.class && !classBindings.containsKey(cls)) {
            cls = cls.getSuperclass();
        }

        if (!classBindings.containsKey(cls)) {
            throw new RuntimeException("Class " + targetClass.getCanonicalName() + " does not have preference bindings");
        }

        //noinspection unchecked
        return (ClassBinding<? super T>)classBindings.get(cls);
    }
}
