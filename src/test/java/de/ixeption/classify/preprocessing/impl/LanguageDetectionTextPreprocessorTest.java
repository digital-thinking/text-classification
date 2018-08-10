package de.ixeption.classify.preprocessing.impl;

import com.google.cloud.Tuple;
import de.ixeption.classify.features.TextFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LanguageDetectionTextPreprocessorTest {

    static LanguageDetectionTextPreprocessor cut;

    LanguageDetectionTextPreprocessorTest() throws IOException {
    }

    @BeforeAll
    public static void before() {
        try {
            cut = new LanguageDetectionTextPreprocessor();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static Stream<Tuple<String, String>> getTestData() {
        return Stream.of(
                Tuple.of("Das ist ein Bier", "de"),
                Tuple.of("That's a beer", "en"),
                Tuple.of("C'est une bière", "fr"),
                Tuple.of("Questa è una birra", "it"),
                Tuple.of("Questa das kann beer to be", null));

    }

    @ParameterizedTest(name = "{index}: Impl Class: {0}")
    @MethodSource("getTestData")
    void preprocess(Tuple<String, String> tuple) {
        assertThat(cut.preprocess(new TextFeature(tuple.x())).getLanguage()).isEqualTo(tuple.y());

    }
}