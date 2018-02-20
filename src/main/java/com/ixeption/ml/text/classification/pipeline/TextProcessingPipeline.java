package com.ixeption.ml.text.classification.pipeline;

import com.ixeption.ml.text.classification.features.TextFeature;
import com.ixeption.ml.text.classification.features.WordIndexing;
import smile.math.SparseArray;

import java.io.Serializable;

public interface TextProcessingPipeline extends WordIndexing, Serializable {
    SparseArray process(TextFeature textFeature);
}
