// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.apt;

import com.slimgears.slimapt.ClassGenerator;
import com.slimgears.slimapt.TypeUtils;
import com.slimgears.slimprefs.PreferenceProvider;
import com.slimgears.slimprefs.PreferenceValue;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by ditskovi on 1/28/2016.
 *
 */
public class PreferencesClassGenerator extends ClassGenerator<PreferencesClassGenerator> {
    public PreferencesClassGenerator(ProcessingEnvironment processingEnvironment, TypeName preferencesInterface) {
        super(processingEnvironment);
        super.addInterfaces(preferencesInterface);
    }

    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces) {
        PropertyFinder propertyFinder = new PropertyFinder();
        TypeElement preferenceInterface = interfaces[0];
        preferenceInterface.accept(propertyFinder, null);
        Collection<PropertyFinder.PropertyDescriptor> properties = propertyFinder.getProperties();

        MethodSpec.Builder ctorBuilder = MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(PreferenceProvider.class, "provider");

        for (PropertyFinder.PropertyDescriptor prop : properties) {
            String preferenceFieldName = prop.getName() + "Preference";
            builder.addField(
                    ParameterizedTypeName.get(ClassName.get(PreferenceValue.class), TypeUtils.box(prop.getType())),
                    preferenceFieldName,
                    Modifier.FINAL, Modifier.PRIVATE);
            ctorBuilder.addCode(
                    "$L = provider.getPreference(\"$L.$L\", $T.class);\n",
                    preferenceFieldName,
                    preferenceInterface.getSimpleName().toString(),
                    prop.getName(),
                    TypeUtils.box(prop.getType()));

            if (prop.hasGetter()) {
                builder.addMethod(MethodSpec
                        .methodBuilder(prop.getGetterName())
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(prop.getType())
                        .addCode("return $L.get();\n", preferenceFieldName)
                        .build());
            }

            if (prop.hasSetter()) {
                builder.addMethod(MethodSpec
                        .methodBuilder(prop.getSetterName())
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(prop.getType(), prop.getName())
                        .addCode("$L.set($L);\n", preferenceFieldName, prop.getName())
                        .build());
            }
        }

        builder.addMethod(ctorBuilder.build());
    }
}
