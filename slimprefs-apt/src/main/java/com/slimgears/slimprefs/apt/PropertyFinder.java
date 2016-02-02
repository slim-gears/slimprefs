// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.apt;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.slimgears.slimapt.ElementVisitorBase;
import com.slimgears.slimapt.TypeUtils;
import com.squareup.javapoet.TypeName;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;

/**
 * Created by ditskovi on 1/30/2016.
 *
 */
public class PropertyFinder extends ElementVisitorBase<Void, Void> {
    private final Map<String, PropertyDescriptor> properties = new HashMap<>();

    class PropertyDescriptor {
        String name;
        ExecutableElement getter;
        ExecutableElement setter;

        TypeName getType() {
            if (hasGetter()) {
                return TypeName.get(getter.getReturnType());
            } else {
                return TypeName.get(setter.getParameters().get(0).asType());
            }
        }

        String getGetterName() {
            return hasGetter() ? getter.getSimpleName().toString() : null;
        }

        String getSetterName() {
            return hasSetter() ? setter.getSimpleName().toString() : null;
        }

        boolean hasGetter() {
            return getter != null;
        }

        boolean hasSetter() {
            return setter != null;
        }

        String getName() {
            return TypeUtils.toCamelCase(name);
        }
    }

    @Override
    public Void visitExecutable(ExecutableElement element, Void param) {
        String name = element.getSimpleName().toString();
        if (name.startsWith("get")) {
            visitGetter(element, name.substring(3));
        } else if (name.startsWith("is")) {
            visitGetter(element, name.substring(2));
        } else if (name.startsWith("set")) {
            visitSetter(element, name.substring(3));
        }
        return null;
    }

    public Collection<PropertyDescriptor> getProperties() {
        return Stream.of(properties.values())
                .collect(Collectors.toList());
    }

    private void visitGetter(ExecutableElement getter, String name) {
        PropertyDescriptor descriptor = getDescriptor(name);
        descriptor.getter = getter;
    }

    private void visitSetter(ExecutableElement setter, String name) {
        PropertyDescriptor descriptor = getDescriptor(name);
        descriptor.setter = setter;
    }

    private PropertyDescriptor getDescriptor(String name) {
        PropertyDescriptor descriptor = properties.getOrDefault(name, null);
        if (descriptor == null) {
            descriptor = new PropertyDescriptor();
            descriptor.name = name;
            properties.put(name, descriptor);
        }
        return descriptor;
    }
}
