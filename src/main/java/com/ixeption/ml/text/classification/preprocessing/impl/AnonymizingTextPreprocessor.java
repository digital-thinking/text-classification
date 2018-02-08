package com.ixeption.ml.text.classification.preprocessing.impl;

import com.ixeption.ml.text.classification.preprocessing.TextPreprocessor;

import java.util.Set;

public class AnonymizingTextPreprocessor implements TextPreprocessor {
    @Override
    public String preprocess(String input, Set<String> replaceRegEx, String language) {
        return input;
    }
}
