// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.apt;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.collect.ImmutableMap;
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
    private final static Map<String, PropertyDescriptorInitializer> PROPERTY_INITIALIZERS =
            ImmutableMap.<String, PropertyDescriptorInitializer>builder()
                    .put("get", PropertyDescriptor::getter)
                    .put("is", PropertyDescriptor::getter)
                    .put("set", PropertyDescriptor::setter)
                    .put("has", PropertyDescriptor::containsWith)
                    .put("contains", PropertyDescriptor::containsWith)
                    .put("remove", PropertyDescriptor::removeWith)
                    .put("clear", PropertyDescriptor::removeWith)
                    .build();

    interface PropertyDescriptorInitializer {
        void setMethod(PropertyDescriptor propertyDescriptor, ExecutableElement method);
    }

    class PropertyDescriptor {
        private String name;
        private ExecutableElement getter;
        private ExecutableElement setter;
        private ExecutableElement remover;
        private ExecutableElement existence;

        TypeName getType() {
            if (hasGet()) {
                return TypeName.get(getter.getReturnType());
            } else {
                return TypeName.get(setter.getParameters().get(0).asType());
            }
        }

        String getGetterName() {
            return hasGet() ? getter.getSimpleName().toString() : null;
        }

        String getSetterName() {
            return hasSet() ? setter.getSimpleName().toString() : null;
        }

        String getRemoverName() {
            return hasRemove() ? remover.getSimpleName().toString() : null;
        }

        String getContainsName() {
            return hasContains() ? existence.getSimpleName().toString() : null;
        }

        boolean hasGet() {
            return getter != null;
        }

        boolean hasSet() {
            return setter != null;
        }

        boolean hasRemove() {
            return remover != null;
        }

        boolean hasContains() {
            return existence != null;
        }

        String getName() {
            return TypeUtils.toCamelCase(name);
        }

        void setter(ExecutableElement setter) {
            this.setter = setter;
        }

        void getter(ExecutableElement getter) {
            this.getter = getter;
        }

        void removeWith(ExecutableElement remover) {
            this.remover = remover;
        }

        void containsWith(ExecutableElement existence) {
            this.existence = existence;
        }
    }

    @Override
    public Void visitExecutable(ExecutableElement element, Void param) {
        String name = element.getSimpleName().toString();
        String prefix = getFirstWord(name);
        String propName = name.substring(prefix.length());

        if (PROPERTY_INITIALIZERS.containsKey(prefix)) {
            PROPERTY_INITIALIZERS.get(prefix).setMethod(getDescriptor(propName), element);
        }

        return null;
    }

    public Collection<PropertyDescriptor> getProperties() {
        return Stream.of(properties.values())
                .collect(Collectors.toList());
    }

    private PropertyDescriptor getDescriptor(String name) {
        if (properties.containsKey(name)) return properties.get(name);
        PropertyDescriptor descriptor = new PropertyDescriptor();
        descriptor.name = name;
        properties.put(name, descriptor);
        return descriptor;
    }

    private static String getFirstWord(String name) {
        int index = 0;
        while (Character.isLowerCase(name.charAt(index))) ++index;
        return name.substring(0, index);
    }
}
