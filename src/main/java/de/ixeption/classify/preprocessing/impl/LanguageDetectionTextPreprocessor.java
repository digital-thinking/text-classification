package de.ixeption.classify.preprocessing.impl;

import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.preprocessing.TextPreprocessor;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;


@ThreadSafe
public class LanguageDetectionTextPreprocessor implements TextPreprocessor {

    private final LanguageDetector _languageDetector;

    public LanguageDetectionTextPreprocessor() throws IOException {
        LanguageDetectorBuilder builder = LanguageDetectorBuilder.create(NgramExtractors.standard())//
                .shortTextAlgorithm(50)//
                .prefixFactor(1.5)//
                .suffixFactor(2.0)//
                .minimalConfidence(0.8)//
                .withProfiles(new LanguageProfileReader().readAllBuiltIn());
        _languageDetector = builder.build();
    }

    @Override
    public TextFeature preprocess(TextFeature textFeature) {
        LdLocale lang = _languageDetector.detect(textFeature.getText()).orNull();
        if (lang != null) {
            textFeature.setLanguage(lang.getLanguage());
        }
        return textFeature;
    }
}
