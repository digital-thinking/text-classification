package com.ixeption.ml.text.classification.pipeline.impl;

import com.ixeption.ml.text.classification.TextFeature;
import org.junit.jupiter.api.Test;
import smile.math.SparseArray;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTextPipelineTest {

    DefaultTextPipeline cut = new DefaultTextPipeline();

    @Test
    void process() {
        SparseArray array = cut.process(new TextFeature("Hallo this is a test"));
        int index = cut.getIndex("hallo");
        assertThat(cut.getToken(index)).isEqualTo("hallo");
        assertThat(array.size()).isEqualTo(5);
    }
}