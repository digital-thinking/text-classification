package com.ixeption.ml.text.classification.pipeline.impl;

import com.ixeption.ml.text.classification.features.TextFeature;
import com.ixeption.ml.text.classification.features.WordIndexing;
import com.ixeption.ml.text.classification.features.impl.HashTrickBagOfWordsFeatureExtractor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTextPipelineTest {

    DefaultTextPipeline cut = new DefaultTextPipeline(new HashTrickBagOfWordsFeatureExtractor(1337, 2, 2));

    @Test
    void testFeatureIndex() throws WordIndexing.IndexerException {
        cut.process(new TextFeature("Hallo this is a test"));
        int index = cut.getIndex("hallo");
        assertThat(cut.getToken(index)).isEqualTo("hallo");
    }
}