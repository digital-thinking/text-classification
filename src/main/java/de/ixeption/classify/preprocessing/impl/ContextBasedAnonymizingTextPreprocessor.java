package de.ixeption.classify.preprocessing.impl;

import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.preprocessing.TextPreprocessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.math.distance.EditDistance;
import smile.nlp.tokenizer.BreakIteratorTokenizer;

import java.util.Locale;
import java.util.Map;


public class ContextBasedAnonymizingTextPreprocessor implements TextPreprocessor {

    private static final Logger log = LoggerFactory.getLogger(ContextBasedAnonymizingTextPreprocessor.class);
    private final EditDistance editDistance;
    private final BreakIteratorTokenizer breakIteratorTokenizer = new BreakIteratorTokenizer(Locale.ENGLISH);
    private final int maxLength;
    private final int minLength;
    private double maxDifferencePercent;

    /**
     * @param minLength            the minimum count of characters to calculate the distance
     * @param maxLength            the maximum count of characters to calculate the distance
     * @param maxDifferencePercent words are considered as equal if the editDistance/lengthOfWord is lower than the given threshold
     */
    public ContextBasedAnonymizingTextPreprocessor(int minLength, int maxLength, double maxDifferencePercent) {
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.maxDifferencePercent = maxDifferencePercent;
        editDistance = new EditDistance(maxLength);
    }

    @Override
    public TextFeature preprocess(TextFeature textFeature) {
        for (Map.Entry<String, String> ex : textFeature.getExcludes().entrySet()) {
            String toExclude = ex.getKey();
            if (toExclude != null && toExclude.length() >= minLength && toExclude.length() < maxLength) {
                for (String token : breakIteratorTokenizer.split(textFeature.getText())) {
                    if (token != null && token.length() >= minLength && token.length() < maxLength) {
                        double distance = editDistance.d(toExclude.toLowerCase(), token.toLowerCase());
                        if (distance / token.length() < maxDifferencePercent) {
                            log.debug("Distance-{}: {}:{}->{}", ex.getValue(), token, toExclude, distance / token.length());
                            textFeature.setText(textFeature.getText().replaceAll(token, ex.getValue()));
                        }
                    }
                }
            }
        }
        return textFeature;
    }

}
