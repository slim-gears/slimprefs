// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs;

import java.util.Date;

/**
 * Created by ditskovi on 1/29/2016.
 *
 */
@Preferences
public interface DummyPreferences {
    int getAge();
    void setAge(int age);

    String getName();
    void setName(String name);

    Date getInstallationDate();
    void setInstallationDate(Date date);

    boolean isRegistered();
    void setRegistered(boolean registered);
}
