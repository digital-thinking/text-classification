package de.ixeption.classify.pipeline;

import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.features.WordIndexing;
import smile.math.SparseArray;

public interface TextProcessingPipeline extends WordIndexing {
    SparseArray process(TextFeature textFeature);

    TokenizedText prepare(TextFeature textFeature);

    TokenizedText processTokens(TokenizedText tokenizedText);

    TokenizedText tokenize(TextFeature textFeature);

    TextFeature preprocess(TextFeature textFeature);
}
