package de.ixeption.classify.preprocessing.impl;

import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.preprocessing.ContextType;
import de.ixeption.classify.preprocessing.TextPreprocessor;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.math.distance.EditDistance;
import smile.nlp.tokenizer.BreakIteratorTokenizer;

import java.util.Locale;

public class ContextBasedAnonymizingTextPreprocessor implements TextPreprocessor {

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
        for (Pair<String, ContextType> ex : textFeature.getExcludes()) {
            for (String token : breakIteratorTokenizer.split(textFeature.getText())) {
                if (ex.getKey() != null && token.length() >= minLength && token.length() < maxLength) {
                    double distance = editDistance.d(ex.getKey(), token);
                    if (distance / token.length() < 0.25) {
                        log.debug("Distance-{}: {}:{}->{}", ex.getValue(), token, ex.getKey(), distance / token.length());
                        textFeature.setText(textFeature.getText().replaceAll(token, ex.getValue().name()));
                    }
                }
            }
        }
        return textFeature.getText();
    }

}
