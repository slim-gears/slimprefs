// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs;

import java.util.Date;

/**
 * Created by ditskovi on 1/29/2016.
 *
 */
public class DummyInjectionTarget {
    boolean registered;

    @BindPreference int age = 15;
    @BindPreference String name;
    @BindPreference Date installationDate;

    @BindPreference void onRegisteredChanged(boolean registered) {
        this.registered = registered;
    }
}
