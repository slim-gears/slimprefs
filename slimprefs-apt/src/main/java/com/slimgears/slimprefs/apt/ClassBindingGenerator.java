// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.apt;

import com.google.common.collect.Iterables;
import com.slimgears.slimapt.ClassGenerator;
import com.slimgears.slimapt.TypeUtils;
import com.slimgears.slimprefs.BindPreference;
import com.slimgears.slimprefs.PreferenceBinding;
import com.slimgears.slimprefs.PreferenceProvider;
import com.slimgears.slimprefs.internal.AbstractClassBinding;
import com.slimgears.slimprefs.internal.ClassBinding;
import com.slimgears.slimprefs.internal.CompositePreferenceBinding;
import com.slimgears.slimprefs.internal.PreferenceObserver;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by ditskovi on 1/31/2016.
 *
 */
public class ClassBindingGenerator extends ClassGenerator<ClassBindingGenerator> {
    private final List<BindingDescriptor> bindings = new ArrayList<>();
    private final TypeName targetTypeName;
    private final String targetClassSimpleName;

    class BindingDescriptor {
        protected final Element element;
        protected final String bindingName;
        protected final TypeName bindingType;
        protected final Object defaultValue;
        protected final CodeBlock defaultValueCode;
        protected final CodeBlock observerCode;
        protected final BindPreference bindingAnnotation;

        private BindingDescriptor(Element element, TypeName bindingType, String bindingName, Object defaultValue, CodeBlock defaultValueCode, CodeBlock observerCode) {
            this.element = element;
            this.bindingAnnotation = element.getAnnotation(BindPreference.class);
            this.bindingName = getBindingName(bindingAnnotation, bindingName);
            this.bindingType = bindingType;
            this.defaultValueCode = defaultValueCode;
            this.observerCode = observerCode;
            this.defaultValue = defaultValue;
        }

        BindingDescriptor(VariableElement element) {
            this(element,
                 TypeName.get(element.asType()),
                 element.getSimpleName().toString(),
                 TypeUtils.defaultValue(TypeName.get(element.asType())),
                 CodeBlock.builder().add("target.$L", element.getSimpleName()).build(),
                 CodeBlock.builder().add("target.$L = value;", element.getSimpleName()).build());
        }

        BindingDescriptor(ExecutableElement element) {
            this(element,
                 TypeName.get(element.getParameters().get(0).asType()),
                 element.getParameters().get(0).getSimpleName().toString(),
                 null,
                 CodeBlock.builder().add("null").build(),
                 CodeBlock.builder().add("target.$L(value);", element.getSimpleName()).build());
            if (bindingAnnotation.twoWay()) {
                throw new RuntimeException(
                        "Two way binding cannot be used with methods (method: " +
                        element.getEnclosingElement().getSimpleName() + "." +
                        element.getSimpleName() + ")");
            }
        }

        CodeBlock build() {
            CodeBlock.Builder codeBuilder = CodeBlock.builder();
            TypeName boxedBindingType = TypeUtils.box(bindingType);
            build(codeBuilder, boxedBindingType);
            return codeBuilder.build();
        }

        void build(CodeBlock.Builder builder, TypeName boxedBindingType) {
            builder
                    .indent()
                    .add("bindMember(provider.getPreference($L, $T.class), ", bindingName, boxedBindingType)
                    .add(defaultValueCode)
                    .add(", $L, ", defaultValue)
                    .add("new $T<$T>() {\n", PreferenceObserver.class, boxedBindingType)
                    .indent()
                    .add("@$T\n", Override.class)
                    .add("public void onChanged($T value) { ", boxedBindingType)
                    .add(observerCode)
                    .add(" }\n")
                    .unindent()
                    .add("})")
                    .unindent();
        }

        private String getBindingName(BindPreference binding, String defaultName) {
            if (!"".equals(binding.key())) {
                return "\"" + binding.key() + "\"";
            } else if (binding.keyRes() != 0) {
                return "0x" + Integer.toHexString(binding.keyRes());
            } else {
                return "\"" + targetClassSimpleName + "." + defaultName + "\"";
            }
        }
    }

    class TwoWayBindingDescriptor extends BindingDescriptor {
        TwoWayBindingDescriptor(VariableElement element) {
            super(element);
        }

        @Override
        void build(CodeBlock.Builder builder, TypeName boxedBindingType) {
            builder
                    .indent()
                        .add("bindMemberTwoWay(provider.getPreference($L, $T.class),\n", bindingName, boxedBindingType)
                        .indent()
                            .add("new ValueProvider<$T>() {\n", boxedBindingType)
                            .indent()
                                .add("@$T\n", Override.class)
                                .add("public $T get() { return $L; }\n", boxedBindingType, defaultValueCode)
                                .add("},\n")
                            .unindent()
                            .add("$L,\n", defaultValue)
                            .add("new $T<$T>() {\n", PreferenceObserver.class, boxedBindingType)
                            .indent()
                                .add("@$T\n", Override.class)
                                .add("public void onChanged($T value) { ", boxedBindingType)
                                .add(observerCode)
                                .add(" }\n")
                            .unindent()
                            .add("})")
                        .unindent()
                    .unindent();
        }
    }

    public ClassBindingGenerator(ProcessingEnvironment processingEnvironment, TypeElement targetClass) {
        super(processingEnvironment);
        targetTypeName = TypeUtils.getTypeName(targetClass);
        targetClassSimpleName = targetClass.getSimpleName().toString();
        String packageName = TypeUtils.packageName(targetClass.getQualifiedName().toString());
        String className = targetClass.getSimpleName().toString();
        this.className(packageName, "Generated" + className + "ClassBinding")
            .superClass(ParameterizedTypeName.get(
                    ClassName.get(AbstractClassBinding.class),
                    targetTypeName));
    }

    public TypeName getTargetTypeName() {
        return targetTypeName;
    }

    public void addBinding(Element element) {
        BindPreference bindingAnnotation = element.getAnnotation(BindPreference.class);
        if (element instanceof VariableElement) {
            VariableElement fieldElement = (VariableElement)element;
            bindings.add(bindingAnnotation.twoWay()
                         ? new TwoWayBindingDescriptor(fieldElement)
                         : new BindingDescriptor(fieldElement));
        }
        else if (element instanceof ExecutableElement) bindings.add(new BindingDescriptor((ExecutableElement)element));
        else throw new RuntimeException("Element type is not supported: " + element.getClass().getCanonicalName());
    }

    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces) {
        builder.addField(
            FieldSpec
                .builder(ParameterizedTypeName.get(ClassName.get(ClassBinding.class), targetTypeName), "INSTANCE")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL)
                .initializer("new $T()", getTypeName())
                .build());
        builder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());
        MethodSpec.Builder bindMethodBuilder = MethodSpec
            .methodBuilder("bind")
            .returns(PreferenceBinding.class)
            .addParameter(PreferenceProvider.class, "provider")
            .addParameter(targetTypeName, "target", Modifier.FINAL)
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addCode("return $T.create(\n", CompositePreferenceBinding.class);

        for (BindingDescriptor binding : Iterables.limit(bindings, bindings.size() - 1)) {
            bindMethodBuilder
                .addCode(binding.build())
                .addCode(",\n");
        }

        for (BindingDescriptor binding : Iterables.skip(bindings, bindings.size() - 1)) {
            bindMethodBuilder.addCode(binding.build());
        }

        bindMethodBuilder.addCode(");\n");
        builder.addMethod(bindMethodBuilder.build());
    }
}
