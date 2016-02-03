// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.apt;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.slimgears.slimapt.AnnotationProcessorBase;
import com.slimgears.slimprefs.BindPreference;
import com.slimgears.slimprefs.GeneratePreferenceInjector;
import com.slimgears.slimprefs.PreferenceInjector;

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
@SupportedAnnotationTypes({"com.slimgears.slimprefs.BindPreference", "com.slimgears.slimprefs.GeneratePreferenceInjector"})
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

        if (classGenerators.size() > 0) {
            try {
                for (ClassBindingGenerator generator : classGenerators.asMap().values()) {
                    System.out.println("Generating " + generator.getTypeName().simpleName());
                    generator.build();
                }

                processAnnotation(GeneratePreferenceInjector.class, roundEnv);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
        InjectorGenerator injectorGenerator = new InjectorGenerator(processingEnv, typeElement, classGenerators.asMap().values());
        injectorGenerator.build();
        return true;
    }

    private void validateInjectorGeneratorElement(TypeElement typeElement) {
        Types typeUtils = processingEnv.getTypeUtils();
        TypeElement injectorElement = processingEnv.getElementUtils().getTypeElement(PreferenceInjector.class.getCanonicalName());
        if (!typeUtils.isAssignable(typeElement.asType(), injectorElement.asType())) {
            throw new IllegalArgumentException(String.format("%1s does not extend %2s", typeElement.getQualifiedName(), injectorElement.getSimpleName()));
        }
    }
}
