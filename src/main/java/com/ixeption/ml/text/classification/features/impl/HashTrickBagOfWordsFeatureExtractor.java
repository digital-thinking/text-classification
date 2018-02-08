package com.ixeption.ml.text.classification.features.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.ixeption.ml.text.classification.features.FeatureUtils;
import com.ixeption.ml.text.classification.features.TextFeatureExtractor;
import org.apache.commons.lang3.StringUtils;
import smile.math.SparseArray;
import smile.nlp.dictionary.EnglishStopWords;
import smile.nlp.dictionary.StopWords;
import smile.nlp.normalizer.Normalizer;
import smile.nlp.normalizer.SimpleNormalizer;
import smile.nlp.stemmer.PorterStemmer;
import smile.nlp.stemmer.Stemmer;
import smile.nlp.tokenizer.BreakIteratorTokenizer;
import smile.nlp.tokenizer.Tokenizer;

import java.util.Arrays;

public class HashTrickBagOfWordsFeatureExtractor implements TextFeatureExtractor {

    private final Stemmer _stemmer = new PorterStemmer();
    private final Tokenizer _tokenizer = new BreakIteratorTokenizer();
    private final StopWords _stopWords = EnglishStopWords.DEFAULT;
    private final Normalizer _normalizer = SimpleNormalizer.getInstance();
    private BiMap<Integer, String> _indexToName = Maps.synchronizedBiMap(HashBiMap.create());
    private final int seed;
    private final int nGrams;
    private final int tokenMinLength;

    public HashTrickBagOfWordsFeatureExtractor(int seed, int nGrams, int tokenMinLength) {
        this.seed = seed;
        this.nGrams = nGrams;
        this.tokenMinLength = tokenMinLength;

    }

    @Override
    public SparseArray extract(String input) {
        SparseArray features = new SparseArray();

        String[] tokens = {};
        String normalized = _normalizer != null ? _normalizer.normalize(input) : input;
        normalized = normalized.toLowerCase();
        tokens = Arrays.//
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
                .toArray(String[]::new);

        for (String token : tokens) {
            int index = FeatureUtils.hash32(token.getBytes(), token.length(), seed);
            features.set(index, 1.0f);
            _indexToName.put(index, token);
        }

        // n-gram
        int n = nGrams;
        for (int k = 0; k < (tokens.length - n + 1); k++) {
            String ngram = "";
            int end = k + n;
            for (int j = k; j < end; j++) {
                if (ngram.isEmpty()) {
                    ngram = tokens[j];
                } else {
                    ngram = ngram + " " + tokens[j];
                }
            }
            //Add n-gram to a list
            int index = FeatureUtils.hash32(ngram.getBytes(), ngram.length(), seed);
            _indexToName.put(index, ngram);
            features.set(index, 1.0f);
        }

        return features;
    }

    @Override
    public int getIndex(String s) {
        return _indexToName.inverse().get(s);
    }

    @Override
    public String getToken(int index) {
        return _indexToName.get(index);
    }
}
