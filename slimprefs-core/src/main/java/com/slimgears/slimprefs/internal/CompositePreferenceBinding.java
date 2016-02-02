// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.internal;

import com.slimgears.slimprefs.PreferenceBinding;

/**
 * Created by ditskovi on 1/30/2016.
 *
 */
public class CompositePreferenceBinding implements PreferenceBinding {
    private final PreferenceBinding[] bindings;

    private CompositePreferenceBinding(PreferenceBinding[] bindings) {
        this.bindings = bindings;
    }

    public static PreferenceBinding create(PreferenceBinding... bindings) {
        return new CompositePreferenceBinding(bindings);
    }

    @Override
    public void unbind() {
        for (PreferenceBinding binding : bindings) {
            binding.unbind();
        }
    }
}
