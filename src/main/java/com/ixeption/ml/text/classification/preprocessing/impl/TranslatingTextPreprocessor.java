package com.ixeption.ml.text.classification.preprocessing.impl;

import com.ixeption.ml.text.classification.preprocessing.TextPreprocessor;

import java.util.Set;

public class TranslatingTextPreprocessor implements TextPreprocessor {
    private final String targetLanguage;

    public TranslatingTextPreprocessor(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    @Override
    public String preprocess(String input, Set<String> replaceRegEx, String language) {
        return input;
    }
}
