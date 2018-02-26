package de.ixeption.classify.preprocessing.impl;

import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.preprocessing.TextPreprocessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.math.distance.EditDistance;
import smile.nlp.tokenizer.BreakIteratorTokenizer;

import java.util.Locale;

public class ContextBasedAnonymizingTextPreprocessor implements TextPreprocessor {

    public static final String PLACEHOLDER_PERSONAL_CONTEXT = "PLACEHOLDER_PERSONAL_CONTEXT";
    private static final Logger log = LoggerFactory.getLogger(ContextBasedAnonymizingTextPreprocessor.class);
    private final EditDistance editDistance;
    private final BreakIteratorTokenizer breakIteratorTokenizer = new BreakIteratorTokenizer(Locale.ENGLISH);
    private final int maxLength;
    private final int minLength;

    public ContextBasedAnonymizingTextPreprocessor(int minLength, int maxLength) {
        this.maxLength = maxLength;
        this.minLength = minLength;
        editDistance = new EditDistance(maxLength);
    }

    @Override
    public String preprocess(TextFeature textFeature) {
        for (String ex : textFeature.getExcludes()) {
            for (String token : breakIteratorTokenizer.split(textFeature.getText())) {
                if (token.length() >= minLength && token.length() < maxLength) {
                    double distance = editDistance.d(ex, token);
                    if (distance / token.length() < 0.25) {
                        log.debug("Distance: " + token + ":" + ex + "->" + distance / token.length());
                        textFeature.setText(textFeature.getText().replaceAll(token, PLACEHOLDER_PERSONAL_CONTEXT));
                    }
                }
            }
        }
        return textFeature.getText();
    }
}
