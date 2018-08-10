package de.ixeption.classify.tokenization.impl;

import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.features.impl.AbstractBagOfWordsFeatureExtractor;
import de.ixeption.classify.tokenization.TextTokenizer;
import de.ixeption.classify.tokenization.Token;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


public class StemmingNGramTextTokenizer implements TextTokenizer {

    public static final String TOKEN_SEPERATOR = " ";
    private static final Logger log = LoggerFactory.getLogger(AbstractBagOfWordsFeatureExtractor.class);
    private final Stemmer _stemmer = new PorterStemmer();
    private final Tokenizer _tokenizer = new BreakIteratorTokenizer();
    private final StopWords _stopWords = EnglishStopWords.GOOGLE;
    private final Normalizer _normalizer = SimpleNormalizer.getInstance();

    private final int nGramsCount;
    private final int tokenMinLength;
    private final int tokenMaxLength;

    public StemmingNGramTextTokenizer(int nGramsCount, int tokenMinLength, int tokenMaxLength) {
        this.nGramsCount = nGramsCount;
        this.tokenMinLength = tokenMinLength;
        this.tokenMaxLength = tokenMaxLength;

    }

    @Override
    public Token[] tokenize(TextFeature textFeature) {
        return extractTokens(textFeature.getText());
    }

    public Token[] extractTokens(String input) {
        String normalized = _normalizer != null ? _normalizer.normalize(input) : input;
        normalized = normalized.toLowerCase();
        List<String> tokens = Arrays.//
                stream(_tokenizer.split(normalized))//
                .map(_stemmer::stem) //
                .filter(StringUtils::isNotEmpty) // filter empty strings
                .filter(s -> s.length() >= this.tokenMinLength) // filter short
                .filter(s -> s.length() < this.tokenMaxLength) // filter long
                .filter(s -> !_stopWords.contains(s))// stop words
                .filter(s -> !StringUtils.isNumeric(s))// filter numbers
                .filter(s -> {
                    Character.UnicodeScript script = Character.UnicodeScript.of(s.codePointAt(0));
                    return script.equals(Character.UnicodeScript.COMMON) || script.equals(Character.UnicodeScript.LATIN);
                })// latin
                .collect(toList());

        addNGrams(tokens);
        return tokens.stream().map(Token::new).toArray(Token[]::new);
    }

    private void addNGrams(List<String> tokens) {
        if (getnGramsCount() == 0) {
            return;
        }
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

    public int getnGramsCount() {
        return nGramsCount;
    }
}
