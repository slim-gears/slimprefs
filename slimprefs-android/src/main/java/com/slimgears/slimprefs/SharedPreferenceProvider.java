// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.slimgears.slimprefs.internal.DefaultPreferenceValue;
import com.slimgears.slimprefs.internal.DefaultPreferenceValueFactory;
import com.slimgears.slimprefs.internal.PreferenceValueFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ditskovi on 1/29/2016.
 *
 */
public class SharedPreferenceProvider implements PreferenceProvider {
    private final SharedPreferences sharedPreferences;
    private final Map<Class, PreferenceValueFactory> valueFactories = new HashMap<>();
    private final Map<String, PreferenceValue> preferenceValues = new HashMap<>();
    private final Context context;

    interface SharedPreferenceValueGetter<T> {
        T getValue(SharedPreferences sharedPreferences, String key, T defaultValue);
    }

    interface SharedPreferenceValueSetter<T> {
        void setValue(SharedPreferences.Editor editor, String key, T value);
    }

    interface Converter<From, To> {
        To convert(From from);
    }

    abstract class ConvertiblePreferenceValueFactory<From, To> implements PreferenceValueFactory<From> {
        private final PreferenceValueFactory<To> destValueFactory;
        private final Class<To> destValueType;

        protected ConvertiblePreferenceValueFactory(Class<To> destValueType) {
            this.destValueFactory = getValueFactory(destValueType);
            this.destValueType = destValueType;
        }

        @Override
        public PreferenceValue<From> createPreferenceValue(String key, Class<From> valueType) {
            PreferenceValue<To> destValue = destValueFactory.createPreferenceValue(key, destValueType);
            DefaultPreferenceValue.ValueGetter<From> getter = key1 -> {
                To val = destValue.get();
                return val != null ? decode(val) : null;
            };

            return DefaultPreferenceValue.<From>builder()
                    .key(key)
                    .getter(getter)
                    .setter((key1, value) -> destValue.set(encode(value)))
                    .existence(sharedPreferences::contains)
                    .observable(observable(getter))
                    .build();
        }

        protected abstract To encode(From value);
        protected abstract From decode(To value);
    }

    class CustomConvertiblePreferenceValueFactory<From, To> extends ConvertiblePreferenceValueFactory<From, To> {
        private final Converter<From, To> encoder;
        private final Converter<To, From> decoder;

        CustomConvertiblePreferenceValueFactory(Class<To> destValueType, Converter<From, To> encoder, Converter<To, From> decoder) {
            super(destValueType);
            this.encoder = encoder;
            this.decoder = decoder;
        }

        @Override
        protected To encode(From value) {
            return encoder.convert(value);
        }

        @Override
        protected From decode(To value) {
            return decoder.convert(value);
        }
    }

    abstract class EncodablePreferenceValueFactory<From> extends ConvertiblePreferenceValueFactory<From, byte[]> {
        protected EncodablePreferenceValueFactory() {
            super(byte[].class);
        }
    }

    class SerializablePreferenceValueFactory<T extends Serializable> extends EncodablePreferenceValueFactory<T> {
        @Override
        protected byte[] encode(T value) {
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                try (ObjectOutputStream writer = new ObjectOutputStream(stream)) {
                    writer.writeObject(value);
                }
                return stream.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected T decode(byte[] bytes) {
            try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
                try (ObjectInputStream reader = new ObjectInputStream(stream)) {
                    //noinspection unchecked
                    return (T)reader.readObject();
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class ParcelablePreferenceValueFactory<T extends Parcelable> extends EncodablePreferenceValueFactory<T> {
        private final Parcelable.Creator<T> creator;

        ParcelablePreferenceValueFactory(Parcelable.Creator<T> creator) {
            this.creator = creator;
        }

        @Override
        protected byte[] encode(T value) {
            Parcel parcel = Parcel.obtain();
            value.writeToParcel(parcel, 0);
            return parcel.marshall();
        }

        @Override
        protected T decode(byte[] bytes) {
            Parcel parcel = Parcel.obtain();
            parcel.unmarshall(bytes, 0, bytes.length);
            return creator.createFromParcel(parcel);
        }
    }

    public SharedPreferenceProvider(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        registerProvider(String.class, null, SharedPreferences::getString, SharedPreferences.Editor::putString);
        registerProvider(Long.class, 0L, SharedPreferences::getLong, SharedPreferences.Editor::putLong);
        registerProvider(Float.class, 0f, SharedPreferences::getFloat, SharedPreferences.Editor::putFloat);
        registerProvider(Boolean.class, false, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean);
        registerProvider(Integer.class, 0, SharedPreferences::getInt, SharedPreferences.Editor::putInt);
        registerConvertible(Date.class, Long.class, Date::getTime, Date::new);
        registerConvertible(byte[].class, String.class, bytes -> Base64.encodeToString(bytes, Base64.DEFAULT), str -> Base64.decode(str, Base64.DEFAULT));
        registerConvertible(Double.class, String.class, Object::toString, Double::parseDouble);
        registerParcelable(Bundle.class, Bundle.CREATOR);
    }

    protected <T> DefaultPreferenceValue.ValueGetter<T> getter(SharedPreferenceValueGetter<T> getter, T defaultValue) {
        return key -> sharedPreferences.contains(key) ? getter.getValue(sharedPreferences, key, defaultValue) : defaultValue;
    }

    protected <T> DefaultPreferenceValue.ValueSetter<T> setter(SharedPreferenceValueSetter<T> setter) {
        return (key, value) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            setter.setValue(editor, key, value);
            editor.apply();
        };
    }

    protected <T> DefaultPreferenceValue.ValueObservable<T> observable(DefaultPreferenceValue.ValueGetter<T> getter) {
        return (key, observer) -> {
            SharedPreferences.OnSharedPreferenceChangeListener listener =
                    (sharedPreferences, key1) -> {
                        if (key.equals(key1)) {
                            observer.onChanged(getter.getValue(key1));
                        }
                    };
            sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
            return () -> sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
        };
    }

    protected <T> DefaultPreferenceValue.ValueObservable<T> observable(SharedPreferenceValueGetter<T> getter, T defaultValue) {
        return (key, observer) -> {
            SharedPreferences.OnSharedPreferenceChangeListener listener =
                    (sharedPreferences, key1) -> {
                        if (key.equals(key1)) {
                            observer.onChanged(getter.getValue(sharedPreferences, key1, defaultValue));
                        }
                    };

            sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
            return () -> sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
        };
    }

    protected <T> PreferenceValueFactory<T> registerProvider(Class<T> valueType,
                                                             T defaultValue,
                                                             SharedPreferenceValueGetter<T> getter,
                                                             SharedPreferenceValueSetter<T> setter) {
        return registerProvider(valueType, DefaultPreferenceValueFactory.<T>builder()
                .getter(getter(getter, defaultValue))
                .setter(setter(setter))
                .existence(sharedPreferences::contains)
                .observable(observable(getter, defaultValue))
                .build());
    }

    protected <T extends Parcelable> PreferenceValueFactory<T> registerParcelable(Class<T> valueType, Parcelable.Creator<T> creator) {
        return registerProvider(valueType, new ParcelablePreferenceValueFactory<>(creator));
    }

    protected <T> PreferenceValueFactory<T> registerProvider(Class<T> valueType, PreferenceValueFactory<T> valueFactory) {
        valueFactories.put(valueType, valueFactory);
        return valueFactory;
    }

    protected <From, To> PreferenceValueFactory<From> registerConvertible(Class<From> fromValueType, Class<To> toValueType, Converter<From, To> encoder, Converter<To, From> decoder) {
        return registerProvider(fromValueType, new CustomConvertiblePreferenceValueFactory<>(toValueType, encoder, decoder));
    }

    protected <T extends Serializable> PreferenceValueFactory<T> registerSerializable(Class<T> valueType) {
        return registerProvider(valueType, new SerializablePreferenceValueFactory<>());
    }

    @Override
    public <T> PreferenceValue<T> getPreference(String key, Class<T> valueType) {
        if (preferenceValues.containsKey(key)) {
            //noinspection unchecked
            return (PreferenceValue<T>)preferenceValues.get(key);
        }

        PreferenceValue<T> value = getValueFactory(valueType).createPreferenceValue(key, valueType);
        preferenceValues.put(key, value);
        return value;
    }

    @Override
    public <T> PreferenceValue<T> getPreference(int keyRes, Class<T> valueType) {
        return getPreference(context.getString(keyRes), valueType);
    }

    private <T> PreferenceValueFactory<T> getValueFactory(Class<T> valueType) {
        if (!valueFactories.containsKey(valueType)) {
            throw new RuntimeException("Type " + valueType.getCanonicalName() + " is not supported");
        }
        //noinspection unchecked
        return (PreferenceValueFactory<T>)valueFactories.get(valueType);
    }
}
