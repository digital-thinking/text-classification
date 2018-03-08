package de.ixeption.classify.preprocessing.impl;

import com.google.api.gax.retrying.RetrySettings;
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
    private final Translate translator;
    private int numTranslations = 0;
    private int numCharacters = 0;
    private boolean notCallApi = false;

    /**
     * Builds a translator, which only translates texts, where the source language is not equal to the target language
     * To detect the source language <a href="https://github.com/optimaize/language-detector">language-detector</a> is used
     * If no language is detected (which happens in short sentences) and the possible source language equals the target language, no translation is executed
     *
     * @param targetLanguage    the target language
     * @param minimalConfidence confidence which is necessary for the translation not to be executed
     * @param maxAttempts       retries on the api
     * @param projectId         google projectId
     * @throws IOException
     */
    public GoogleTranslateTextPreprocessor(String targetLanguage, double minimalConfidence, int maxAttempts, String projectId) throws IOException {
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
        translator = TranslateOptions.newBuilder()
                .setRetrySettings(RetrySettings.newBuilder().setMaxAttempts(maxAttempts).build())
                .setProjectId(projectId)
                .build()
                .getService();
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
                    translator.translate(
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
