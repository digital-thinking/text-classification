package de.ixeption.classify.features;

import java.util.Set;

public class TextFeature {

    private String language;
    private String text;
    private Set<String> excludes;

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

    public Set<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(Set<String> excludes) {
        this.excludes = excludes;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
