// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.apt;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.slimgears.slimapt.AnnotationProcessorBase;
import com.slimgears.slimprefs.BindPreference;
import com.slimgears.slimprefs.PreferenceFactory;
import com.slimgears.slimprefs.PreferenceInjector;
import com.slimgears.slimprefs.PreferenceInjectorFactory;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Types;

/**
 * Created by ditskovi on 1/30/2016.
 *
 */
@SupportedAnnotationTypes({"com.slimgears.slimprefs.BindPreference", "com.slimgears.slimprefs.PreferenceFactory"})
public class BindPreferenceMemberAnnotationProcessor extends AnnotationProcessorBase {
    private final LoadingCache<TypeElement, ClassBindingGenerator> classGenerators = CacheBuilder
            .newBuilder()
            .build(new ClassBindingGeneratorFactory());

    class ClassBindingGeneratorFactory extends CacheLoader<TypeElement, ClassBindingGenerator> {
        @Override
        public ClassBindingGenerator load(TypeElement key) {
            try {
                return new ClassBindingGenerator(processingEnv, key);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processAnnotation(BindPreference.class, roundEnv);

        try {
            for (ClassBindingGenerator generator : classGenerators.asMap().values()) {
                System.out.println("Generating " + generator.getTypeName().simpleName());
                generator.build();
            }

            processAnnotation(PreferenceFactory.class, roundEnv);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private boolean processMember(Element memberElement) throws ExecutionException {
        classGenerators
                .get((TypeElement)memberElement.getEnclosingElement())
                .addBinding(memberElement);
        return true;
    }

    @Override
    protected boolean processMethod(ExecutableElement methodElement) throws ExecutionException {
        return processMember(methodElement);
    }

    @Override
    protected boolean processField(VariableElement fieldElement) throws ExecutionException {
        return processMember(fieldElement);
    }

    @Override
    protected boolean processType(TypeElement typeElement) throws ExecutionException, IOException {
        validateInjectorGeneratorElement(typeElement);
        InjectorFactoryGenerator injectorFactoryGenerator = new InjectorFactoryGenerator(processingEnv, typeElement, classGenerators.asMap().values());
        injectorFactoryGenerator.build();
        return true;
    }

    private void validateInjectorGeneratorElement(TypeElement typeElement) {
        Types typeUtils = processingEnv.getTypeUtils();
        TypeElement injectorFactoryElement = processingEnv.getElementUtils().getTypeElement(PreferenceInjectorFactory.class.getCanonicalName());
        if (!typeUtils.isAssignable(typeElement.asType(), injectorFactoryElement.asType())) {
            throw new IllegalArgumentException(String.format("%1s does not extend %2s", typeElement.getQualifiedName(), injectorFactoryElement.getSimpleName()));
        }
    }
}
