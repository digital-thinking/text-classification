package de.ixeption.classify.pipeline;

import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.features.WordIndexing;
import smile.math.SparseArray;

public interface TextProcessingPipeline extends WordIndexing {
    SparseArray process(TextFeature textFeature);
}
