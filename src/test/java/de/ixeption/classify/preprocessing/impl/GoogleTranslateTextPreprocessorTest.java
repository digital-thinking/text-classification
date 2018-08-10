package de.ixeption.classify.preprocessing.impl;

import de.ixeption.classify.features.TextFeature;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class GoogleTranslateTextPreprocessorTest {

    GoogleTranslateTextPreprocessor cut = new GoogleTranslateTextPreprocessor("en", 3, "myProjectId");

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
        TextFeature textFeature = new TextFeature("Hello my name is xyz, I´m from canada");
        textFeature.setLanguage("en");
        cut.preprocess(textFeature);
        assertThat(cut.getNumTranslations()).isEqualTo(0);
    }

    @Test
    void testIsAlreadyEnglishWithLocale() {
        cut.setNotCallApi(true);
        TextFeature textFeature = new TextFeature("Hello name");
        textFeature.setLanguage("en");
        cut.preprocess(textFeature);
        assertThat(cut.getNumTranslations()).isEqualTo(0);
    }

    @Test
    void testTranslationNoApi() {
        cut.setNotCallApi(true);
        cut.preprocess(new TextFeature("Hallo ich bin der Heinrich"));
        assertThat(cut.getNumTranslations()).isEqualTo(1);
    }

    @Test
    public void testWrongLanguage() {
        assertThrows(IllegalArgumentException.class, () -> new GoogleTranslateTextPreprocessor("osdif", 3, "myProjectId"));
    }

    @Test
    public void testEnglishLanguage() throws IOException {
        new GoogleTranslateTextPreprocessor("en", 3, "myProjectId");
    }

}