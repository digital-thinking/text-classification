package de.ixeption.classify.features;

import java.util.HashMap;
import java.util.Map;


public class TextFeature {

    private String language;
    private String text;
    private Map<String, String> excludes = new HashMap<>();

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

    public Map<String, String> getExcludes() {
        return excludes;
    }

    public void setExcludes(Map<String, String> excludes) {
        this.excludes = excludes;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
