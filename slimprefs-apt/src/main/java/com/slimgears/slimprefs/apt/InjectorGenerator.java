// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.apt;

import com.slimgears.slimapt.ClassGenerator;
import com.slimgears.slimapt.TypeUtils;
import com.slimgears.slimprefs.PreferenceProvider;
import com.slimgears.slimprefs.internal.AbstractPreferenceInjector;
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
public class InjectorGenerator extends ClassGenerator<InjectorGenerator> {
    private final Collection<ClassBindingGenerator> classBindingGenerators;

    public InjectorGenerator(ProcessingEnvironment processingEnvironment, TypeElement baseInterface, Collection<ClassBindingGenerator> classBindingGenerators) {
        super(processingEnvironment);
        this.classBindingGenerators = classBindingGenerators;

        String qualifiedName = TypeUtils.qualifiedName(baseInterface);
        String packageName = TypeUtils.packageName(qualifiedName);
        String simpleName = "Generated" + TypeUtils.simpleName(qualifiedName).replace('$', '_');

        this
            .className(packageName, simpleName)
            .superClass(AbstractPreferenceInjector.class)
            .addInterfaces(baseInterface);
    }

    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces) {
        MethodSpec.Builder ctorBuilder = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(PreferenceProvider.class, "provider")
            .addCode("super(provider);\n");

        for (ClassBindingGenerator generator : classBindingGenerators) {
            ctorBuilder.addCode("addBinding($T.class, $T.INSTANCE);\n", generator.getTargetTypeName(), generator.getTypeName());
        }

        builder.addMethod(ctorBuilder.build());
    }
}
