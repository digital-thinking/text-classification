package com.ixeption.ml.text.classification.pipeline;

import com.ixeption.ml.text.classification.TextFeature;
import com.ixeption.ml.text.classification.features.WordIndexing;
import smile.math.SparseArray;

import java.util.Set;

public interface TextProcessingPipeline extends WordIndexing{
    SparseArray process(TextFeature textFeature);
}
