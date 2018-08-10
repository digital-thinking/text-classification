package de.ixeption.classify.preprocessing.impl;

import de.ixeption.classify.features.TextFeature;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static de.ixeption.classify.preprocessing.impl.ContextBasedAnonymizingTextPreprocessorTest.ContextType.CONTEXT_PERSONAL;
import static de.ixeption.classify.preprocessing.impl.ContextBasedAnonymizingTextPreprocessorTest.ContextType.CONTEXT_PHONE;
import static org.assertj.core.api.Assertions.assertThat;

class ContextBasedAnonymizingTextPreprocessorTest {

    ContextBasedAnonymizingTextPreprocessor cut = new ContextBasedAnonymizingTextPreprocessor(5, 15, 0.25);

    @Test
    void testReplacement() {
        TextFeature textFeature = new TextFeature("Hello my name is Christian. \n I am from Germany. \n My tel number is 0176 5884661");

        HashMap<String, String> stringHashMap = new HashMap<>();
        stringHashMap.put("Christian", CONTEXT_PERSONAL.name());
        stringHashMap.put("Germany", CONTEXT_PERSONAL.name());
        stringHashMap.put("01765884661", CONTEXT_PHONE.name());
        textFeature.setExcludes(stringHashMap);

        String res = cut.preprocess(textFeature).getText();
        assertThat(res).doesNotContain("Germany", "Christian");
        assertThat(res).contains(CONTEXT_PERSONAL.name());
    }

    @Test
    void testTooLong() {
        TextFeature textFeature = new TextFeature("Hello my name is Tolongnameforareplacementreally");
        HashMap<String, String> stringHashMap = new HashMap<>();
        stringHashMap.put("Tolongnameforareplacementreally", CONTEXT_PERSONAL.name());
        textFeature.setExcludes(stringHashMap);
        String res = cut.preprocess(textFeature).getText();
        assertThat(res).contains("Tolongnameforareplacementreally");
    }

    @Test
    void testTooShort() {
        TextFeature textFeature = new TextFeature("Hello my name is Tolo");
        HashMap<String, String> stringHashMap = new HashMap<>();
        stringHashMap.put("Tolo", CONTEXT_PERSONAL.name());
        textFeature.setExcludes(stringHashMap);
        String res = cut.preprocess(textFeature).getText();
        assertThat(res).contains("Tolo");
    }

    @Test
    void testMinBoundary() {
        TextFeature textFeature = new TextFeature("Hello my name is Toloo");
        HashMap<String, String> stringHashMap = new HashMap<>();
        stringHashMap.put("Toloo", CONTEXT_PERSONAL.name());
        textFeature.setExcludes(stringHashMap);
        String res = cut.preprocess(textFeature).getText();
        assertThat(res).doesNotContain("Toloo");
    }

    @Test
    void testMaxBoundary() {
        TextFeature textFeature = new TextFeature("Hello my name is Tololololololol");
        HashMap<String, String> stringHashMap = new HashMap<>();
        stringHashMap.put("Tololololololol", CONTEXT_PERSONAL.name());
        textFeature.setExcludes(stringHashMap);
        String res = cut.preprocess(textFeature).getText();
        assertThat(res).contains("Tololololololol");
    }

    static public enum ContextType {
        CONTEXT_PERSONAL, CONTEXT_PHONE
    }


}