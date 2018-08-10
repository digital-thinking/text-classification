package de.ixeption.classify.features;

public interface WordIndexing {
    /**
     * Be aware, that this method does not preprocess your input.
     * This means that the token you want to lookup should preprocessed by {@link de.ixeption.classify.tokenization.TextTokenizer}
     *
     * @param s the token
     * @return the index of the token
     * @throws IndexerException if the token is not indexed
     */
    int getIndex(String s) throws IndexerException;

    /**
     * @param index the index
     * @return the token or in case of a collision a comma separated string with the matched tokens
     * @throws IndexerException in case of the the index is not known
     */
    String getToken(int index) throws IndexerException;


    public class IndexerException extends Exception {
        public IndexerException(String s) {
            super(s);
        }
    }
}
