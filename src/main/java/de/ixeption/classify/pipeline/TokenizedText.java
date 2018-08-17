package de.ixeption.classify.pipeline;

public class TokenizedText {
    private String[] tokens;
    private String language;

    public TokenizedText(String[] tokens, String language) {
        this.tokens = tokens;
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String[] getTokens() {
        return tokens;
    }

    public void setTokens(String[] tokens) {
        this.tokens = tokens;
    }
}
