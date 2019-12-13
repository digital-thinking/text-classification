package de.ixeption.classify.preprocessing.impl;

import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.preprocessing.TextPreprocessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AnonymizingTextPreprocessor implements TextPreprocessor {

    @Override
    public TextFeature preprocess(TextFeature textFeature) {
        String text = textFeature.getText();
        for (ReplaceRegEx regEx : ReplaceRegEx.values()) {
            Matcher matcher = regEx.p.matcher(text);
            // force the placeholder to be recognized as a single token
            text = matcher.replaceAll(" " + regEx.placeHolder + " ");
        }
        textFeature.setText(text);
        return textFeature;
    }

    public enum ReplaceRegEx {
        // @formatter:off
        PATTERN_EMAIL("PLACEHOLDER_EMAIL_STRICT", Pattern.compile("(?:[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")),
        PATTERN_EMAIL_BROAD("PLACEHOLDER_EMAIL_BROAD", Pattern.compile("([a-zA-Z0-9!#$%&'*+\\/=?^_`{}~-]+)( *\\W| *)(\\bat\\b|@)(\\W *| *)([a-z-A-Z-0-9]+)( *\\W| *)(\\.|\\(?dot\\)?)( *\\W| *)([a-zA-Z]{2,})")),
        PATTERN_URL("PLACEHOLDER_URL", Pattern.compile("(www\\.|http:\\/\\/|https:\\/\\/)([a-zA-Z0-9\\-]*\\.)+[a-zA-Z0-9]{2,4}(\\/[a-zA-Z0-9=.?&-]*)?", Pattern.CASE_INSENSITIVE)),
        PATTERN_IBAN("PLACEHOLDER_IBAN", Pattern.compile("\\b[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{4}[0-9]{7}([a-zA-Z0-9]?){0,16}|[a-zA-Z]{2}\\\\s*\\\\d{2}\\\\s*([0-9a-zA-Z]{4}\\\\s*){4}[0-9a-zA-Z]{2}|[a-zA-Z]{2}\\\\s*([0-9a-zA-Z]{4}\\\\s*){5}\\b|\\b[a-s-A-Z]{2} ?\\d{2}[ \\t]?([0-9A-Z]{2,4}\\s*){3,6}[ \t]\\b")),
        PATTERN_PHONE("PLACEHOLDER_PHONE", Pattern.compile("(\\+\\s?\\d{2}).*([0-9\\-_ ]{6,})|((\\(\\d{3}\\)?)|(\\d{3}))([\\s-./]?)(\\d{3})([\\s-./]?)(\\d{4,})|(\\d{3,}\\/\\d{3,})", Pattern.CASE_INSENSITIVE)),
        ;
        // @formatter:on

        public final Pattern p;
        public final String placeHolder;

        ReplaceRegEx(final String placeHolder, Pattern pattern) {
            this.p = pattern;
            this.placeHolder = placeHolder;
        }

    }
}
