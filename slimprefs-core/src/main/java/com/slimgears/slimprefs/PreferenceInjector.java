// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs;

/**
 * Created by ditskovi on 1/31/2016.
 *
 */
public interface PreferenceInjector {
    <T> PreferenceBinding bind(T target);
}
