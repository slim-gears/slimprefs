package com.slimgears.slimprefs.apt;

import com.slimgears.slimapt.AnnotationProcessingTestBase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by Denis on 25/09/2015.
 *
 */
@RunWith(JUnit4.class)
public class PreferencesClassAnnotationProcessorTest extends AnnotationProcessingTestBase {
    @Test
    public void forInterfaceAnnotatedWith_Preferences_generatesPreferencesImplementation() {
        testAnnotationProcessing(
            processedWith(new PreferencesClassAnnotationProcessor()),
            inputFiles("DummyPreferences.java"),
            expectedFiles("GeneratedDummyPreferences.java"));
    }

    @Test
    public void forMembersAnnotatedWith_BindPreferences_classBindingShouldBeGenerated() {
        testAnnotationProcessing(
            processedWith(new BindPreferenceMemberAnnotationProcessor()),
            inputFiles("DummyInjectionTarget.java"),
            expectedFiles("GeneratedDummyInjectionTargetClassBinding.java"));
    }

    @Test
    public void forGeneratePreferenceInjectorAnnotation_generatesPreferenceInjectorImplementation() {
        testAnnotationProcessing(
            processedWith(new BindPreferenceMemberAnnotationProcessor()),
            inputFiles("DummyInjectionTarget.java", "DummyPreferenceInjectorFactory.java"),
            expectedFiles("GeneratedDummyPreferenceInjectorFactory.java"));
    }
}
