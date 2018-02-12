package com.ixeption.ml.text.classification.preprocessing.impl;

import com.ixeption.ml.text.classification.features.TextFeature;
import com.ixeption.ml.text.classification.preprocessing.TextPreprocessor;

public class TranslatingTextPreprocessor implements TextPreprocessor {
    private final String targetLanguage;

    public TranslatingTextPreprocessor(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    @Override
    public String preprocess(TextFeature textFeature) {
        return textFeature.getText();
    }
}
