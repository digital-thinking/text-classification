package de.ixeption.classify.pipeline;

import de.ixeption.classify.tokenization.Token;

public class TokenizedText {
    private final Token[] tokens;

    private final String language;

    public TokenizedText(Token[] tokens, String language) {
        this.tokens = tokens;
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public Token[] getTokens() {
        return tokens;
    }
}
