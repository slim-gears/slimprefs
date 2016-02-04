// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs;

/**
 * Created by ditskovi on 2/4/2016.
 *
 */
public interface PreferenceInjectorFactory {
    PreferenceInjector createInjector(PreferenceProvider preferenceProvider);
}
