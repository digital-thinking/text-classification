package de.ixeption.classify.postprocessing;

import de.ixeption.classify.pipeline.TokenizedText;

@FunctionalInterface
public interface TokenProcessor {
    TokenizedText process(TokenizedText token);
}
