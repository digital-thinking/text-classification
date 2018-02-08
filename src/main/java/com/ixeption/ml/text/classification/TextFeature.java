package com.ixeption.ml.text.classification;

import java.util.Collections;
import java.util.Set;

public class TextFeature {
    private final Set<String> replaceRegEx;
    private final String language;
    private String text;

    public TextFeature(String text, Set<String> replaceRegEx, String language) {
        this.text = text;
        this.replaceRegEx = replaceRegEx;
        this.language = language;
    }

    public TextFeature(String text) {
        this.text = text;
        this.replaceRegEx = Collections.emptySet();
        this.language = null;

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Set<String> getReplaceRegEx() {
        return replaceRegEx;
    }

    public String getLanguage() {
        return language;
    }
}
