// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.internal;

import com.slimgears.slimprefs.PreferenceValue;

/**
 * Created by ditskovi on 1/29/2016.
 *
 */
public interface PreferenceValueFactory<T> {
    PreferenceValue<T> createPreferenceValue(String key, Class<T> valueType);
}
