// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.apt;

import com.slimgears.slimapt.ClassGenerator;
import com.slimgears.slimapt.TypeUtils;
import com.slimgears.slimprefs.PreferenceInjectorFactory;
import com.slimgears.slimprefs.PreferenceProvider;
import com.slimgears.slimprefs.internal.AbstractPreferenceInjectorFactory;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by ditskovi on 1/31/2016.
 *
 */
public class InjectorFactoryGenerator extends ClassGenerator<InjectorFactoryGenerator> {
    private final Collection<ClassBindingGenerator> classBindingGenerators;

    public InjectorFactoryGenerator(ProcessingEnvironment processingEnvironment, TypeElement baseInterface, Collection<ClassBindingGenerator> classBindingGenerators) {
        super(processingEnvironment);
        this.classBindingGenerators = classBindingGenerators;

        String qualifiedName = TypeUtils.qualifiedName(baseInterface);
        String packageName = TypeUtils.packageName(qualifiedName);
        String simpleName = "Generated" + TypeUtils.simpleName(qualifiedName).replace('$', '_');

        this
            .className(packageName, simpleName)
            .superClass(AbstractPreferenceInjectorFactory.class)
            .addInterfaces(baseInterface);
    }

    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces) {
        MethodSpec.Builder ctorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE);

        for (ClassBindingGenerator generator : classBindingGenerators) {
            ctorBuilder.addCode("addBinding($T.class, $T.INSTANCE);\n", generator.getTargetTypeName(), generator.getTypeName());
        }

        builder
                .addField(FieldSpec
                         .builder(PreferenceInjectorFactory.class, "INSTANCE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                         .initializer("new $T()", getTypeName())
                         .build())
                .addMethod(ctorBuilder.build());
    }
}
