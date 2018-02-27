package de.ixeption.classify.features;

import java.util.Collections;
import java.util.Set;

public class TextFeature {

    private String possibleLocale;
    private String text;
    private Set<String> excludes = Collections.emptySet();

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

    public Set<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(Set<String> excludes) {
        this.excludes = excludes;
    }

    public String getPossibleLocale() {
        return possibleLocale;
    }

    public void setPossibleLocale(String possibleLocale) {
        this.possibleLocale = possibleLocale;
    }
}
