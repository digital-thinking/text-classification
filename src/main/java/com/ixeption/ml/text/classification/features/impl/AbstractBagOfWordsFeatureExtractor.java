package com.ixeption.ml.text.classification.features.impl;

import com.ixeption.ml.text.classification.features.TextFeatureExtractor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.math.SparseArray;
import smile.nlp.dictionary.EnglishStopWords;
import smile.nlp.dictionary.StopWords;
import smile.nlp.normalizer.Normalizer;
import smile.nlp.normalizer.SimpleNormalizer;
import smile.nlp.stemmer.PorterStemmer;
import smile.nlp.stemmer.Stemmer;
import smile.nlp.tokenizer.BreakIteratorTokenizer;
import smile.nlp.tokenizer.Tokenizer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public abstract class AbstractBagOfWordsFeatureExtractor implements TextFeatureExtractor, Serializable {

    private static final Logger log = LoggerFactory.getLogger(AbstractBagOfWordsFeatureExtractor.class);

    private final Stemmer _stemmer = new PorterStemmer();
    private final Tokenizer _tokenizer = new BreakIteratorTokenizer();
    private final StopWords _stopWords = EnglishStopWords.GOOGLE;
    private final Normalizer _normalizer = SimpleNormalizer.getInstance();

    private final int nGrams;
    private final int tokenMinLength;


    public AbstractBagOfWordsFeatureExtractor(int nGrams, int tokenMinLength) {
        this.nGrams = nGrams;
        this.tokenMinLength = tokenMinLength;

    }

    @Override
    public SparseArray extract(String input) {
        SparseArray features = new SparseArray();
        String[] tokens = getTokens(input);

        for (String token : tokens) {
            try {
                features.set(getIndex(token), 1.0f);
            } catch (IndexerException e) {
                log.warn("Ignored non indexed word :" + token, e);
            }
        }

        return features;
    }

    public String[] getTokens(String input) {

        String normalized = _normalizer != null ? _normalizer.normalize(input) : input;
        normalized = normalized.toLowerCase();
        List<String> tokens = Arrays.//
                stream(_tokenizer.split(normalized))//
                .map(_stemmer::stem) //
                .filter(StringUtils::isNotEmpty) // filter empty strings
                .filter(s -> s.length() >= this.tokenMinLength) // filter short
                .filter(s -> !_stopWords.contains(s))// stop words
                .filter(s -> !StringUtils.isNumeric(s))// filter numbers
                .filter(s -> {
                    Character.UnicodeScript script = Character.UnicodeScript.of(s.codePointAt(0));
                    return script.equals(Character.UnicodeScript.COMMON) || script.equals(Character.UnicodeScript.LATIN);
                })// latin
                .collect(toList());

        // n-gram
        int n = nGrams;
        int tokenSize = tokens.size();
        for (int k = 0; k < (tokenSize - n + 1); k++) {
            String ngram = "";
            int end = k + n;
            for (int j = k; j < end; j++) {
                if (ngram.isEmpty()) {
                    ngram = tokens.get(j);
                } else {
                    ngram = ngram + " " + tokens.get(j);
                }
            }
            tokens.add(ngram);
        }

        return tokens.toArray(new String[0]);
    }

    @Override
    public String getToken(int index) throws IndexerException {
        return getTokenInternal(index);
    }

    protected abstract String getTokenInternal(int index) throws IndexerException;

    @Override
    public int getIndex(String s) throws IndexerException {
        String[] tokens = getTokens(s);
        if (tokens.length == 1) {
            return getIndexInternal(tokens[0]);
        }
        throw new IndexerException("Invalid token " + s);

    }

    protected abstract int getIndexInternal(String token) throws IndexerException;

}
