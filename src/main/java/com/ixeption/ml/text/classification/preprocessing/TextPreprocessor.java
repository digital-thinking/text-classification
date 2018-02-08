package com.ixeption.ml.text.classification.preprocessing;

import com.ixeption.ml.text.classification.TextFeature;

import java.util.Set;

public interface TextPreprocessor {

    String preprocess(TextFeature textFeature);
}
