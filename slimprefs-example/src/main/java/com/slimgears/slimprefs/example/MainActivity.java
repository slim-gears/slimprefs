// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.slimgears.slimprefs.BindPreference;
import com.slimgears.slimprefs.PreferenceBinding;

import java.util.Date;

/**
 * Created by Denis on 22-Apr-15
 * <File Description>
 */
public class MainActivity extends Activity {
    @BindPreference String stringPreferenceDefaultKey;
    @BindPreference(key = "int_preference_explicit_key") int intPreferenceExplicitKey;
    @BindPreference(keyRes = R.string.date_preference_key_res) Date datePreferenceKeyRes;

    private PreferenceBinding preferenceBinding;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);
        preferenceBinding = ((App)getApplication()).getPreferenceInjector().bind(this);
    }

    @Override
    public void onDestroy() {
        preferenceBinding.unbind();
        super.onDestroy();
    }
}
