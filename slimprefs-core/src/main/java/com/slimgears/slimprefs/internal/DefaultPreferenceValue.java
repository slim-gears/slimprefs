// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.internal;

import com.slimgears.slimprefs.PreferenceBinding;
import com.slimgears.slimprefs.PreferenceValue;

/**
 * Created by ditskovi on 1/29/2016.
 *
 */
public class DefaultPreferenceValue<T> implements PreferenceValue<T> {
    private static final PreferenceBinding EMPTY_BINDING = () -> {};

    private final String key;
    private final ValueGetter<T> getter;
    private final ValueSetter<T> setter;
    private final ValueObservable<T> observable;

    public DefaultPreferenceValue(String key, ValueGetter<T> getter, ValueSetter<T> setter, ValueObservable<T> observable) {
        this.key = key;
        this.getter = getter;
        this.setter = setter;
        this.observable = observable;
    }

    @Override
    public T get() {
        return getter != null ? getter.getValue(key) : null;
    }

    @Override
    public void set(T value) {
        if (setter != null) setter.setValue(key, value);
    }

    @Override
    public PreferenceBinding observe(PreferenceObserver<T> observer) {
        if (observable == null) return EMPTY_BINDING;
        PreferenceBinding binding = observable.observe(key, observer);
        T value = get();
        if (value != null) observer.onChanged(value);
        return binding;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public interface ValueGetter<T> {
        T getValue(String key);
    }

    public interface ValueSetter<T> {
        void setValue(String key, T value);
    }

    public interface ValueObservable<T> {
        PreferenceBinding observe(String key, PreferenceObserver<T> observer);
    }

    public static class Builder<T> {
        private String key;
        private ValueGetter<T> getter;
        private ValueSetter<T> setter;
        private ValueObservable<T> observable;

        public Builder<T> key(String key) {
            this.key = key;
            return this;
        }

        public Builder<T> getter(ValueGetter<T> getter) {
            this.getter = getter;
            return this;
        }

        public Builder<T> setter(ValueSetter<T> setter) {
            this.setter = setter;
            return this;
        }

        public Builder<T> observable(ValueObservable<T> observable) {
            this.observable = observable;
            return this;
        }

        public DefaultPreferenceValue<T> build() {
            return new DefaultPreferenceValue<>(key, getter, setter, observable);
        }
    }
}
