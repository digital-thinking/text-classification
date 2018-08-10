package de.ixeption.classify.postprocessing;

import de.ixeption.classify.tokenization.Token;

@FunctionalInterface
public interface TokenProcessor {
    Token[] process(Token[] token);
}
