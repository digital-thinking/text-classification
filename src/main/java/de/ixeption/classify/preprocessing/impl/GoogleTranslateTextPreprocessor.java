package de.ixeption.classify.preprocessing.impl;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.preprocessing.TextPreprocessor;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Using google cloud translation api to translate everything to english
 * To use this service provide either the GOOGLE_APPLICATION_CREDENTIALS or the GOOGLE_API_KEY
 *
 * @see <a href="https://cloud.google.com/translate/">Google cloud translation api</a>
 */
public class GoogleTranslateTextPreprocessor implements TextPreprocessor {

    private static final Logger log = LoggerFactory.getLogger(GoogleTranslateTextPreprocessor.class);
    private final String targetLanguage;

    private final LanguageDetector languageDetector;
    private final Translate translater;
    private int numTranslations = 0;
    private int numCharacters = 0;
    private boolean notCallApi = false;

    public GoogleTranslateTextPreprocessor(String targetLanguage, double minimalConfidence) throws IOException {
        List<LanguageProfile> languageProfiles = null;
        languageProfiles = new LanguageProfileReader().readAllBuiltIn();
        languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(languageProfiles)
                .minimalConfidence(minimalConfidence)
                .build();
        boolean validLanguage = languageProfiles.stream().anyMatch(languageProfile -> languageProfile.getLocale().getLanguage().equals(targetLanguage));
        if (!validLanguage) {
            throw new IOException("Target language not supported");
        }
        this.targetLanguage = targetLanguage;
        translater = TranslateOptions.getDefaultInstance().getService();
    }

    @Override
    public String preprocess(TextFeature textFeature) {
        Optional<LdLocale> detect = languageDetector.detect(textFeature.getText());
        if (detect.isPresent()) {
            if (!detect.get().getLanguage().equals(targetLanguage)) {
                return translate(textFeature.getText());
            }
        } else {
            if (textFeature.getPossibleLocale() != null && !LocaleUtils.toLocale(textFeature.getPossibleLocale()).getLanguage().equals(targetLanguage)) {
                return translate(textFeature.getText());
            }
        }

        return textFeature.getText();

    }

    private String translate(String source) {
        numCharacters += source.length();
        numTranslations++;
        if (!notCallApi) {
            Translation translation =
                    translater.translate(
                            source,
                            Translate.TranslateOption.targetLanguage(targetLanguage));

            return translation.getTranslatedText();
        }
        return source;
    }

    public int getNumTranslations() {
        return numTranslations;
    }

    public int getNumCharacters() {
        return numCharacters;
    }

    /**
     * can be used to estimate the translation throughput
     *
     * @param notCallApi when no notCallApi is set, the translation api is not called
     */
    public void setNotCallApi(boolean notCallApi) {
        this.notCallApi = notCallApi;
    }
}
