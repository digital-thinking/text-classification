package de.ixeption.classify.tokenization;

import de.ixeption.classify.features.TextFeature;


@FunctionalInterface
public interface TextTokenizer {

    Token[] tokenize(TextFeature textFeature);

}
