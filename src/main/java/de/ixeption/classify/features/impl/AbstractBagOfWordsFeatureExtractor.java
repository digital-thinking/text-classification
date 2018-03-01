package de.ixeption.classify.features.impl;

import de.ixeption.classify.features.TextFeatureExtractor;
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

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public abstract class AbstractBagOfWordsFeatureExtractor implements TextFeatureExtractor {

    public static final String TOKEN_SEPERATOR = " ";
    private static final Logger log = LoggerFactory.getLogger(AbstractBagOfWordsFeatureExtractor.class);
    private final Stemmer _stemmer = new PorterStemmer();
    private final Tokenizer _tokenizer = new BreakIteratorTokenizer();
    private final StopWords _stopWords = EnglishStopWords.GOOGLE;
    private final Normalizer _normalizer = SimpleNormalizer.getInstance();

    private final int nGramsCount;
    private final int tokenMinLength;

    public AbstractBagOfWordsFeatureExtractor(int nGramsCount, int tokenMinLength) {
        this.nGramsCount = nGramsCount;
        this.tokenMinLength = tokenMinLength;

    }

    @Override
    public final SparseArray extract(String input) {
        String[] tokens = extractTokens(input);
        SparseArray features = transform(tokens);
        return scale(features);
    }

    protected SparseArray transform(String[] tokens) {
        SparseArray features = new SparseArray();
        for (String token : tokens) {
            try {
                int index = getIndexInternal(token);
                features.set(index, 1.0f);
            } catch (IndexerException e) {
                log.warn("Ignored non indexed word :" + token, e);
            }
        }
        return features;

    }

    protected abstract SparseArray scale(SparseArray features);

    public String[] extractTokens(String input) {
        String normalized = _normalizer != null ? _normalizer.normalize(input) : input;
        normalized = normalized.toLowerCase();
        List<String> tokens = Arrays.//
                stream(_tokenizer.split(normalized))//
                .map(_stemmer::stem) //
                .filter(StringUtils::isNotEmpty) // filter empty strings
                .filter(s -> s.length() >= this.tokenMinLength) // filter short
                .filter(s -> !_stopWords.contains(s))// stop words
                .filter(s -> !StringUtils.isNumeric(s))// filter numbers //TODO map to PlaceHolder
                .filter(s -> {
                    Character.UnicodeScript script = Character.UnicodeScript.of(s.codePointAt(0));
                    return script.equals(Character.UnicodeScript.COMMON) || script.equals(Character.UnicodeScript.LATIN);
                })// latin
                .collect(toList());

        addNGrams(tokens);
        return tokens.toArray(new String[0]);
    }

    protected void addNGrams(List<String> tokens) {
        if (getnGramsCount() == 0) return;
        // n-gram
        int n = getnGramsCount();
        int counts = tokens.size() - n + 1;
        for (int k = 0; k < counts; k++) {
            String ngram = "";
            int end = k + n;
            for (int j = k; j < end; j++) {
                if (ngram.isEmpty()) {
                    ngram = tokens.get(j);
                } else {
                    ngram = ngram + TOKEN_SEPERATOR + tokens.get(j);
                }
            }
            tokens.add(ngram);
        }
    }


    @Override
    public String getToken(int index) throws IndexerException {
        return getTokenInternal(index);
    }

    protected abstract String getTokenInternal(int index) throws IndexerException;


    @Override
    public int getIndex(String s) throws IndexerException {
        return getIndexInternal(s);
    }

    protected abstract int getIndexInternal(String token) throws IndexerException;

    public int getnGramsCount() {
        return nGramsCount;
    }

}
