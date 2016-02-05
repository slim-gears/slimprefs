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
    private final ValueExistence existence;
    private final ValueRemover remover;
    private final ValueObservable<T> observable;

    public DefaultPreferenceValue(
            String key,
            ValueGetter<T> getter,
            ValueSetter<T> setter,
            ValueRemover remover,
            ValueExistence existence,
            ValueObservable<T> observable) {
        this.key = key;
        this.getter = getter;
        this.setter = setter;
        this.remover = remover;
        this.existence = existence;
        this.observable = observable;
    }

    @Override
    public T get() {
        return getter != null ? getter.getValue(key) : null;
    }

    @Override
    public boolean exists() {
        return existence != null && existence.exists(key);
    }

    @Override
    public PreferenceValue<T> set(T value) {
        if (setter != null) setter.setValue(key, value);
        return this;
    }

    @Override
    public PreferenceValue<T> remove() {
        if (remover != null) remover.removeValue(key);
        return this;
    }

    @Override
    public PreferenceValue<T> defaultValue(T value) {
        if (value != null && !exists()) set(value);
        return this;
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

    public interface ValueExistence {
        boolean exists(String key);
    }

    public interface ValueGetter<T> {
        T getValue(String key);
    }

    public interface ValueSetter<T> {
        void setValue(String key, T value);
    }

    public interface ValueRemover {
        void removeValue(String key);
    }

    public interface ValueObservable<T> {
        PreferenceBinding observe(String key, PreferenceObserver<T> observer);
    }

    public static class Builder<T> {
        private String key;
        private ValueGetter<T> getter;
        private ValueSetter<T> setter;
        private ValueRemover remover;
        private ValueExistence existence;
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

        public Builder<T> remover(ValueRemover remover) {
            this.remover = remover;
            return this;
        }

        public Builder<T> existence(ValueExistence existence) {
            this.existence = existence;
            return this;
        }

        public Builder<T> observable(ValueObservable<T> observable) {
            this.observable = observable;
            return this;
        }

        public DefaultPreferenceValue<T> build() {
            return new DefaultPreferenceValue<>(key, getter, setter, remover, existence, observable);
        }
    }
}
