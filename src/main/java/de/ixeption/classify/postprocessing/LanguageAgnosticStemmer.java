package de.ixeption.classify.postprocessing;

import de.ixeption.classify.pipeline.TokenizedText;
import org.tartarus.snowball.SnowballStemmer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class LanguageAgnosticStemmer implements TokenProcessor {

    Map<String, SnowballStemmer> stemmers = new HashMap<>();

    @Override
    public TokenizedText process(TokenizedText tokenizedText) {
        String[] stemmed = new String[tokenizedText.getTokens().length];
        String[] tokens = tokenizedText.getTokens();
        for (int i = 0; i < tokens.length; i++) {
            if (tokenizedText.getLanguage() != null) {
                stemmed[i] = stem(tokens[i], tokenizedText.getLanguage());
            } else {
                stemmed[i] = tokens[i];
            }

        }
        tokenizedText.setTokens(stemmed);
        return tokenizedText;
    }

    public String stem(String s, String lang) {
        try {
            SupportedLanguages finalLangEnum = SupportedLanguages.fromKey(lang);
            if (stemmers.containsKey(finalLangEnum.key)) {
                return stemInternal(s, stemmers.get(finalLangEnum.key));
            } else {
                SnowballStemmer stemmer = (SnowballStemmer) Class.forName(finalLangEnum.clazz).newInstance();
                stemmers.put(finalLangEnum.key, stemmer);
                return stemInternal(s, stemmer);
            }
        } catch (IllegalArgumentException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            // nop
        }
        return s;
    }

    private String stemInternal(String s, SnowballStemmer snowballStemmer) {
        snowballStemmer.setCurrent(s);
        snowballStemmer.stem();
        return snowballStemmer.getCurrent();
    }

    public enum SupportedLanguages {
        DANISH("da"),
        DUTCH("nl"),
        ENGLISH("en"),
        FINNISH("fi"),
        FRENCH("fr"),
        GERMAN("de"),
        HUNGARIAN("hu"),
        ITALIAN("it"),
        NORWEGIAN("no"),
        PORTUGUESE("pt"),
        ROMANIAN("ro"),
        RUSSIAN("ru"),
        SPANISH("es"),
        SWEDISH("sv"),
        TURKISH("tr");

        final String key;
        final String clazz;

        SupportedLanguages(String key) {
            this.key = key;
            this.clazz = String.format("org.tartarus.snowball.ext.%sStemmer", name().toLowerCase());
        }

        public static SupportedLanguages fromKey(String lang) throws IllegalArgumentException {
            return Arrays.stream(SupportedLanguages.values())
                    .filter(supportedLanguages -> supportedLanguages.key.equals(lang))
                    .findFirst().orElseThrow(IllegalArgumentException::new);


        }
    }
}
