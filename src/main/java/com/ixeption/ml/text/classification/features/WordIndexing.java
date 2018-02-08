package com.ixeption.ml.text.classification.features;

public interface WordIndexing {
    int getIndex(String s);

    String getToken(int index);
}
