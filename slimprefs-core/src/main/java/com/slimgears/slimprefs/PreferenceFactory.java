// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ditskovi on 1/31/2016.
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface PreferenceFactory {
}
