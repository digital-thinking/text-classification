package de.ixeption.classify.preprocessing.impl;

import com.google.common.collect.Sets;
import de.ixeption.classify.features.TextFeature;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContextBasedAnonymizingTextPreprocessorTest {

    ContextBasedAnonymizingTextPreprocessor cut = new ContextBasedAnonymizingTextPreprocessor(5, 15);

    @Test
    void testReplacement() {
        TextFeature textFeature = new TextFeature("Hello my name is Christian. \n I am from Germany. \n My tel number is 0176 5884661");
        textFeature.setExcludes(Sets.newHashSet("Christian", "German", "01765884661"));
        String res = cut.preprocess(textFeature);
        assertThat(res).doesNotContain("Germany", "Christian");
    }

    @Test
    void testTooLong() {
        TextFeature textFeature = new TextFeature("Hello my name is Tolongnameforareplacementreally");
        textFeature.setExcludes(Sets.newHashSet("Tolongnameforareplacementreally"));
        String res = cut.preprocess(textFeature);
        assertThat(res).contains("Tolongnameforareplacementreally");
    }


    @Test
    void testTooShort() {
        TextFeature textFeature = new TextFeature("Hello my name is Tolo");
        textFeature.setExcludes(Sets.newHashSet("Tolo"));
        String res = cut.preprocess(textFeature);
        assertThat(res).contains("Tolo");
    }

    @Test
    void testMinBoundary() {
        TextFeature textFeature = new TextFeature("Hello my name is Toloo");
        textFeature.setExcludes(Sets.newHashSet("Toloo"));
        String res = cut.preprocess(textFeature);
        assertThat(res).doesNotContain("Toloo");
    }

    @Test
    void testMaxBoundary() {
        TextFeature textFeature = new TextFeature("Hello my name is Tololololololol");
        textFeature.setExcludes(Sets.newHashSet("Tololololololol"));
        String res = cut.preprocess(textFeature);
        assertThat(res).contains("Tololololololol");
    }


}