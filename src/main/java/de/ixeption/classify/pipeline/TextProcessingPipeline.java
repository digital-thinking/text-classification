package de.ixeption.classify.pipeline;

import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.features.WordIndexing;
import smile.math.SparseArray;

import java.io.Serializable;

public interface TextProcessingPipeline extends WordIndexing, Serializable {
    SparseArray process(TextFeature textFeature);
}
