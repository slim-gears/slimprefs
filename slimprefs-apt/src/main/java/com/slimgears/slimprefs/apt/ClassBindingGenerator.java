// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.apt;

import com.google.common.collect.Iterables;
import com.slimgears.slimapt.ClassGenerator;
import com.slimgears.slimapt.TypeUtils;
import com.slimgears.slimprefs.BindPreference;
import com.slimgears.slimprefs.PreferenceBinding;
import com.slimgears.slimprefs.PreferenceProvider;
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
import javax.naming.Binding;

/**
 * Created by ditskovi on 1/31/2016.
 *
 */
public class ClassBindingGenerator extends ClassGenerator<ClassBindingGenerator> {
    private final List<BindingDescriptor> bindings = new ArrayList<>();
    private final TypeName targetTypeName;
    private final String targetClassSimpleName;

    class BindingDescriptor {
        private final Element element;
        private final String bindingName;
        private final TypeName bindingType;
        private final CodeBlock codeBlock;

        private BindingDescriptor(Element element, TypeName bindingType, String bindingName, String codeTemplate) {
            this.element = element;
            this.bindingName = getBindingName(bindingName);
            this.bindingType = bindingType;
            this.codeBlock = CodeBlock.builder().add(codeTemplate, element.getSimpleName().toString()).build();
        }

        BindingDescriptor(VariableElement element) {
            this(element,
                 TypeName.get(element.asType()),
                 element.getSimpleName().toString(),
                 "target.$L = value;");
        }

        BindingDescriptor(ExecutableElement element) {
            this(element,
                 TypeName.get(element.getParameters().get(0).asType()),
                 element.getParameters().get(0).getSimpleName().toString(),
                 "target.$L(value);");
        }

        CodeBlock build() {
            CodeBlock.Builder codeBuilder = CodeBlock.builder();
            TypeName boxedFieldType = TypeUtils.box(bindingType);
            codeBuilder
                .indent()
                .add("provider.getPreference($L, $T.class)",
                        bindingName,
                        boxedFieldType)
                .add(".observe(new $T<$T>() {\n", PreferenceObserver.class, boxedFieldType)
                .indent()
                .add("@$T\n", Override.class)
                .add("public void onChanged($T value) { ", boxedFieldType)
                .add(codeBlock)
                .add(" }\n")
                .unindent()
                .add("})")
                .unindent();
            return codeBuilder.build();
        }

        private String getBindingName(String defaultName) {
            BindPreference binding = element.getAnnotation(BindPreference.class);
            if (!"".equals(binding.key())) {
                return "\"" + binding.key() + "\"";
            } else if (binding.keyRes() != 0) {
                return "0x" + Integer.toHexString(binding.keyRes());
            } else {
                return "\"" + targetClassSimpleName + "." + defaultName + "\"";
            }
        }
    }

    public ClassBindingGenerator(ProcessingEnvironment processingEnvironment, TypeElement targetClass) {
        super(processingEnvironment);
        targetTypeName = TypeUtils.getTypeName(targetClass);
        targetClassSimpleName = targetClass.getSimpleName().toString();
        String packageName = TypeUtils.packageName(targetClass.getQualifiedName().toString());
        String className = targetClass.getSimpleName().toString();
        this.className(packageName, "Generated" + className + "ClassBinding")
            .superClass(Object.class)
            .addInterfaces(ParameterizedTypeName.get(
                ClassName.get(ClassBinding.class),
                targetTypeName));
    }

    public TypeName getTargetTypeName() {
        return targetTypeName;
    }

    public void addBinding(Element element) {
        if (element instanceof VariableElement) bindings.add(new BindingDescriptor((VariableElement)element));
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
