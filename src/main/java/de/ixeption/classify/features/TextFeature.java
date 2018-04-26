package de.ixeption.classify.features;

import java.util.HashMap;
import java.util.Map;

public class TextFeature {

    private String possibleLocale;
    private String text;
    private Map<String, String> excludes = new HashMap<>();

    public TextFeature(String text, String possibleLocale) {
        this.text = text;

        this.possibleLocale = possibleLocale;
    }

    public TextFeature(String text) {
        this.text = text;
        this.possibleLocale = null;

    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getExcludes() {
        return excludes;
    }

    public void setExcludes(Map<String, String> excludes) {
        this.excludes = excludes;
    }

    public String getPossibleLocale() {
        return possibleLocale;
    }

    public void setPossibleLocale(String possibleLocale) {
        this.possibleLocale = possibleLocale;
    }
}
