package de.ixeption.classify.postprocessing;

import de.ixeption.classify.pipeline.TokenizedText;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LanguageAgnosticStemmerTest {

    private static LanguageAgnosticStemmer cut = new LanguageAgnosticStemmer();


    @Test
    void testStemmingGerman() {
        TokenizedText tokenizedText = new TokenizedText(new String[]{"gehen"}, "de");
        TokenizedText process = cut.process(tokenizedText);
        assertThat(process.getTokens()[0]).isEqualTo("geh");

    }

    @Test
    void testStemmingInvalidLanguage() {
        TokenizedText tokenizedText = new TokenizedText(new String[]{"invalid"}, "rr");
        TokenizedText process = cut.process(tokenizedText);
        assertThat(process.getTokens()[0]).isEqualTo("invalid");

    }

    @Test
    void testStemmingNullLanguage() {
        TokenizedText tokenizedText = new TokenizedText(new String[]{"invalid"}, null);
        TokenizedText process = cut.process(tokenizedText);
        assertThat(process.getTokens()[0]).isEqualTo("invalid");

    }
}