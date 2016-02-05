// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.internal;

import com.slimgears.slimprefs.PreferenceValue;

/**
 * Created by ditskovi on 1/29/2016.
 *
 */
public class DefaultPreferenceValueFactory<T> implements PreferenceValueFactory<T> {
    private final DefaultPreferenceValue.ValueGetter<T> getter;
    private final DefaultPreferenceValue.ValueSetter<T> setter;
    private final DefaultPreferenceValue.ValueRemover remover;
    private final DefaultPreferenceValue.ValueExistence existence;
    private final DefaultPreferenceValue.ValueObservable<T> observable;

    DefaultPreferenceValueFactory(
            DefaultPreferenceValue.ValueGetter<T> getter,
            DefaultPreferenceValue.ValueSetter<T> setter,
            DefaultPreferenceValue.ValueRemover remover,
            DefaultPreferenceValue.ValueExistence existence,
            DefaultPreferenceValue.ValueObservable<T> observable) {
        this.getter = getter;
        this.setter = setter;
        this.remover = remover;
        this.existence = existence;
        this.observable = observable;
    }

    @Override
    public PreferenceValue<T> createPreferenceValue(String key, Class<T> valueType) {
        return DefaultPreferenceValue.<T>builder()
                .key(key)
                .getter(getter)
                .setter(setter)
                .remover(remover)
                .existence(existence)
                .observable(observable)
                .build();
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private DefaultPreferenceValue.ValueGetter<T> getter;
        private DefaultPreferenceValue.ValueSetter<T> setter;
        private DefaultPreferenceValue.ValueRemover remover;
        private DefaultPreferenceValue.ValueExistence existence;
        private DefaultPreferenceValue.ValueObservable<T> observable;

        public Builder<T> getter(DefaultPreferenceValue.ValueGetter<T> getter) {
            this.getter = getter;
            return this;
        }

        public Builder<T> setter(DefaultPreferenceValue.ValueSetter<T> setter) {
            this.setter = setter;
            return this;
        }

        public Builder<T> remover(DefaultPreferenceValue.ValueRemover remover) {
            this.remover = remover;
            return this;
        }

        public Builder<T> existence(DefaultPreferenceValue.ValueExistence existence) {
            this.existence = existence;
            return this;
        }

        public Builder<T> observable(DefaultPreferenceValue.ValueObservable<T> observable) {
            this.observable = observable;
            return this;
        }

        public DefaultPreferenceValueFactory<T> build() {
            return new DefaultPreferenceValueFactory<>(getter, setter, remover, existence, observable);
        }
    }
}
