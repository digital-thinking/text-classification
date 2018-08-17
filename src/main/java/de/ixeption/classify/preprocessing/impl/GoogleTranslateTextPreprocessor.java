package de.ixeption.classify.preprocessing.impl;

import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.preprocessing.TextPreprocessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Using google cloud translation api to translate everything to english
 * To use this service provide either the GOOGLE_APPLICATION_CREDENTIALS or the GOOGLE_API_KEY
 *
 * @see <a href="https://cloud.google.com/translate/">Google cloud translation api</a>
 */
public class GoogleTranslateTextPreprocessor implements TextPreprocessor {

    private static final Logger log = LoggerFactory.getLogger(GoogleTranslateTextPreprocessor.class);
    private final String targetLanguage;
    private final Translate translator;
    private int numTranslations = 0;
    private int numCharacters = 0;
    private boolean notCallApi = false;

    /**
     * Builds a translator, which only translates texts, where the source language is not equal to the target language    *
     *
     * @param targetLanguage the target language    *
     * @param maxAttempts    retries on the api
     * @param projectId      google projectId
     */
    public GoogleTranslateTextPreprocessor(String targetLanguage, int maxAttempts, String projectId) {
        if (targetLanguage.length() > 3) {
            throw new IllegalArgumentException("Invalid language");
        }
        this.targetLanguage = targetLanguage;
        translator = TranslateOptions.newBuilder().setRetrySettings(RetrySettings.newBuilder().setMaxAttempts(maxAttempts).build()).setProjectId(projectId)
                .build().getService();
    }

    @Override
    public TextFeature preprocess(TextFeature textFeature) {
        if (textFeature.getLanguage() != null) {
            if (!textFeature.getLanguage().equals(targetLanguage)) {
                textFeature.setText(translate(textFeature.getText()));
            }
        } else {
            textFeature.setText(translate(textFeature.getText()));
        }
        return textFeature;

    }

    private String translate(String source) {
        numCharacters += source.length();
        numTranslations++;
        if (!notCallApi) {
            Translation translation = translator.translate(source, Translate.TranslateOption.targetLanguage(targetLanguage));

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
