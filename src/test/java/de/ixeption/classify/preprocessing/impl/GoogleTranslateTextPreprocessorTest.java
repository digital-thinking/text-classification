package de.ixeption.classify.preprocessing.impl;

import de.ixeption.classify.features.TextFeature;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GoogleTranslateTextPreprocessorTest {

    GoogleTranslateTextPreprocessor cut = new GoogleTranslateTextPreprocessor("en");

    public GoogleTranslateTextPreprocessorTest() throws IOException {
        cut.setNotCallApi(true);
    }


    @Test
    @Disabled("calls the api")
    void testTranslation() {
        cut.setNotCallApi(false);
        assertThat(cut.preprocess(new TextFeature("Hallo ich bin der Heinrich"))).isEqualTo("Hello I am the Heinrich");
    }

    @Test
    void testIsAlreadyEnglish() {
        cut.setNotCallApi(true);
        cut.preprocess(new TextFeature("Hello my name is xyz, I´m from canada"));
        assertThat(cut.getNumTranslations()).isEqualTo(0);
    }

    @Test
    void testTranslationNoApi() {
        cut.setNotCallApi(true);
        cut.preprocess(new TextFeature("Hallo ich bin der Heinrich"));
        assertThat(cut.getNumTranslations()).isEqualTo(1);
    }

    @Test
    public void testWrongLangauge() {
        assertThrows(IOException.class, () -> new GoogleTranslateTextPreprocessor("osdif"));
    }

    @Test
    public void testEnglishLangauge() throws IOException {
        new GoogleTranslateTextPreprocessor("en");
    }

}