// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs;

import com.slimgears.slimprefs.internal.PreferenceObserver;

/**
 * Created by ditskovi on 1/29/2016.
 *
 */
public interface PreferenceValue<T> {
    T get();
    boolean exists();
    PreferenceValue<T> set(T value);
    PreferenceValue<T> remove();
    PreferenceValue<T> defaultValue(T value);
    PreferenceBinding observe(PreferenceObserver<T> observer);
}
