// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.internal;

import com.slimgears.slimprefs.PreferenceBinding;
import com.slimgears.slimprefs.PreferenceProvider;

/**
 * Created by ditskovi on 1/29/2016.
 *
 */
public interface ClassBinding<T> {
    PreferenceBinding bind(PreferenceProvider provider, T target);
}
