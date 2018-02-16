package com.ixeption.ml.text.classification.features;

public interface WordIndexing {
    int getIndex(String s) throws IndexerException;

    String getToken(int index) throws IndexerException;


    class IndexerException extends Exception {
        public IndexerException(String s) {
            super(s);
        }
    }
}
