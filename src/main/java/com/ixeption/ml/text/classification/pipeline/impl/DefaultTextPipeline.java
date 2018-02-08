package com.ixeption.ml.text.classification.pipeline.impl;

import com.ixeption.ml.text.classification.TextFeature;
import com.ixeption.ml.text.classification.features.TextFeatureExtractor;
import com.ixeption.ml.text.classification.features.impl.HashTrickBagOfWordsFeatureExtractor;
import com.ixeption.ml.text.classification.pipeline.TextProcessingPipeline;
import com.ixeption.ml.text.classification.preprocessing.TextPreprocessor;
import com.ixeption.ml.text.classification.preprocessing.impl.AnonymizingTextPreprocessor;
import com.ixeption.ml.text.classification.preprocessing.impl.TranslatingTextPreprocessor;
import smile.math.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DefaultTextPipeline implements TextProcessingPipeline {

    List<TextPreprocessor> preprocessors = new ArrayList<>();
    TextFeatureExtractor textFeatureExtractor;

    public DefaultTextPipeline() {
        preprocessors.add(new AnonymizingTextPreprocessor());
        preprocessors.add(new TranslatingTextPreprocessor("en"));
        textFeatureExtractor = new HashTrickBagOfWordsFeatureExtractor(1337, 2, 3);

    }

    @Override
    public SparseArray process(TextFeature textFeature) {
        for (TextPreprocessor tp : preprocessors) {
            textFeature.setText(tp.preprocess(textFeature));
        }

        return textFeatureExtractor.extract(textFeature.getText());

    }


    @Override
    public int getIndex(String s) {
        return textFeatureExtractor.getIndex(s);
    }

    @Override
    public String getToken(int index) {
        return textFeatureExtractor.getToken(index);
    }
}
