// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.internal;

import com.slimgears.slimprefs.PreferenceBinding;
import com.slimgears.slimprefs.PreferenceValue;

/**
 * Created by ditskovi on 2/3/2016.
 *
 */
public abstract class AbstractClassBinding<T> implements ClassBinding<T> {
    protected interface ValueProvider<T> {
        T get();
    }

    protected <V> PreferenceBinding bindMember(PreferenceValue<V> preferenceValue, V defaultValue, V classDefaultValue, PreferenceObserver<V> observer) {
        if (defaultValue != null && !defaultValue.equals(classDefaultValue)) {
            preferenceValue.defaultValue(defaultValue);
        }
        return preferenceValue.observe(observer);
    }

    protected <V> PreferenceBinding bindMemberTwoWay(PreferenceValue<V> preferenceValue, ValueProvider<V> provider, V classDefaultValue, PreferenceObserver<V> observer) {
        final V defaultValue = provider.get();
        final PreferenceBinding binding = bindMember(preferenceValue, defaultValue, classDefaultValue, observer);
        return () -> {
            V value = provider.get();
            if (value != null && !value.equals(defaultValue)) {
                preferenceValue.set(value);
            }
            binding.unbind();
        };
    }
}
