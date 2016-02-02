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
    void set(T value);
    PreferenceBinding observe(PreferenceObserver<T> observer);
}
