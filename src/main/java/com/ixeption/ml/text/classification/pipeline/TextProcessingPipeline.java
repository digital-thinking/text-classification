package com.ixeption.ml.text.classification.pipeline;

import com.ixeption.ml.text.classification.features.TextFeature;
import com.ixeption.ml.text.classification.features.WordIndexing;
import smile.math.SparseArray;

public interface TextProcessingPipeline extends WordIndexing{
    SparseArray process(TextFeature textFeature);
}
