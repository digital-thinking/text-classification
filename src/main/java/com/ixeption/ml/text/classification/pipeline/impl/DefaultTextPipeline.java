package com.ixeption.ml.text.classification.pipeline.impl;

import com.google.common.collect.Lists;
import com.ixeption.ml.text.classification.features.TextFeature;
import com.ixeption.ml.text.classification.features.TextFeatureExtractor;
import com.ixeption.ml.text.classification.pipeline.TextProcessingPipeline;
import com.ixeption.ml.text.classification.preprocessing.TextPreprocessor;
import smile.math.SparseArray;

import java.util.List;

public class DefaultTextPipeline implements TextProcessingPipeline {

    private final List<TextPreprocessor> preprocessors;
    private final TextFeatureExtractor textFeatureExtractor;

    public DefaultTextPipeline(TextFeatureExtractor textFeatureExtractor, TextPreprocessor... textPreprocessors) {
        preprocessors = Lists.newArrayList(textPreprocessors);
        this.textFeatureExtractor = textFeatureExtractor;
    }


    @Override
    public SparseArray process(TextFeature textFeature) {
        for (TextPreprocessor tp : preprocessors) {
            textFeature.setText(tp.preprocess(textFeature));
        }
        return textFeatureExtractor.extract(textFeature.getText());

    }


    @Override
    public int getIndex(String s) throws IndexerException {
        return textFeatureExtractor.getIndex(s);
    }

    @Override
    public String getToken(int index) throws IndexerException {
        return textFeatureExtractor.getToken(index);
    }


}
