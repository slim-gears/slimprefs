// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimprefs.apt;

import com.slimgears.slimapt.AnnotationProcessorBase;
import com.slimgears.slimapt.TypeUtils;

import java.io.IOException;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;

/**
 * Created by ditskovi on 1/28/2016.
 *
 */
@SupportedAnnotationTypes("com.slimgears.slimprefs.Preferences")
public class PreferencesClassAnnotationProcessor extends AnnotationProcessorBase {
    @Override
    protected boolean processType(TypeElement typeElement) throws IOException {
        String fullname = typeElement.getQualifiedName().toString();
        new PreferencesClassGenerator(processingEnv, TypeUtils.getTypeName(typeElement))
                .className(TypeUtils.packageName(fullname), "Generated" + TypeUtils.simpleName(fullname))
                .build();
        return true;
    }
}
