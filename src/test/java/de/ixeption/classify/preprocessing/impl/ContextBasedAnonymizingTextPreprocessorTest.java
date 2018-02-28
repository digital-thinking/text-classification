package de.ixeption.classify.preprocessing.impl;

import com.google.common.collect.Sets;
import de.ixeption.classify.features.TextFeature;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import static de.ixeption.classify.preprocessing.ContextType.CONTEXT_PERSONAL;
import static de.ixeption.classify.preprocessing.ContextType.CONTEXT_PHONE;
import static org.assertj.core.api.Assertions.assertThat;

class ContextBasedAnonymizingTextPreprocessorTest {

    ContextBasedAnonymizingTextPreprocessor cut = new ContextBasedAnonymizingTextPreprocessor(5, 15);

    @Test
    void testReplacement() {
        TextFeature textFeature = new TextFeature("Hello my name is Christian. \n I am from Germany. \n My tel number is 0176 5884661");
        textFeature.setExcludes(Sets.newHashSet(Pair.of("Christian", CONTEXT_PERSONAL),
                Pair.of("Germany", CONTEXT_PERSONAL),
                Pair.of("01765884661", CONTEXT_PHONE)));
        String res = cut.preprocess(textFeature);
        assertThat(res).doesNotContain("Germany", "Christian");
        assertThat(res).contains(CONTEXT_PERSONAL.name());
    }

    @Test
    void testTooLong() {
        TextFeature textFeature = new TextFeature("Hello my name is Tolongnameforareplacementreally");
        textFeature.setExcludes(Sets.newHashSet(Pair.of("Tolongnameforareplacementreally", CONTEXT_PERSONAL)));
        String res = cut.preprocess(textFeature);
        assertThat(res).contains("Tolongnameforareplacementreally");
    }


    @Test
    void testTooShort() {
        TextFeature textFeature = new TextFeature("Hello my name is Tolo");
        textFeature.setExcludes(Sets.newHashSet(Pair.of("Tolo", CONTEXT_PERSONAL)));
        String res = cut.preprocess(textFeature);
        assertThat(res).contains("Tolo");
    }

    @Test
    void testMinBoundary() {
        TextFeature textFeature = new TextFeature("Hello my name is Toloo");
        textFeature.setExcludes(Sets.newHashSet(Pair.of("Toloo", CONTEXT_PERSONAL)));
        String res = cut.preprocess(textFeature);
        assertThat(res).doesNotContain("Toloo");
    }

    @Test
    void testMaxBoundary() {
        TextFeature textFeature = new TextFeature("Hello my name is Tololololololol");
        textFeature.setExcludes(Sets.newHashSet(Pair.of("Tololololololol", CONTEXT_PERSONAL)));
        String res = cut.preprocess(textFeature);
        assertThat(res).contains("Tololololololol");
    }


}