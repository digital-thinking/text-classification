package com.ixeption.ml.text.classification.pipeline.impl;

import com.ixeption.ml.text.classification.TextFeature;
import org.junit.jupiter.api.Test;
import smile.math.SparseArray;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTextPipelineTest {

    private DefaultTextPipeline cut = new DefaultTextPipeline();

    @Test
    void testIndexOfTokens() {
        int index = cut.getIndex("hallo");
        assertThat(cut.getToken(index)).isEqualTo("hallo");
    }
}