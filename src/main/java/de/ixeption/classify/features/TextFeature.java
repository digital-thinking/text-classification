package de.ixeption.classify.features;

public class TextFeature {

    private final String language;
    private String text;

    public TextFeature(String text, String language) {
        this.text = text;

        this.language = language;
    }

    public TextFeature(String text) {
        this.text = text;
        this.language = null;

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public String getLanguage() {
        return language;
    }
}
