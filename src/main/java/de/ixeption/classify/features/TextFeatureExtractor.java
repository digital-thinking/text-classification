package de.ixeption.classify.features;

import de.ixeption.classify.tokenization.Token;
import smile.math.SparseArray;

public interface TextFeatureExtractor extends WordIndexing {
    SparseArray extract(Token[] input);

}
