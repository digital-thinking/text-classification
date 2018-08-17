package de.ixeption.classify.tokenization;

import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.pipeline.TokenizedText;


@FunctionalInterface
public interface TextTokenizer {

    TokenizedText tokenize(TextFeature textFeature);

}
