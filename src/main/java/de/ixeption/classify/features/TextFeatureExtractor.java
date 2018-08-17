package de.ixeption.classify.features;

import smile.math.SparseArray;

public interface TextFeatureExtractor extends WordIndexing {
    SparseArray extract(String[] input);

}
