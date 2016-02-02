// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.internal;

/**
 * Created by ditskovi on 1/29/2016.
 *
 */
public interface PreferenceObserver<T> {
    void onChanged(T value);
}
