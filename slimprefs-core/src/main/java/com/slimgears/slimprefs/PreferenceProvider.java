// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs;

/**
 * Created by ditskovi on 1/29/2016.
 *
 */
public interface PreferenceProvider {
    <T> PreferenceValue<T> getPreference(String key, Class<T> valueType);
    <T> PreferenceValue<T> getPreference(int keyRes, Class<T> valueType);
}
