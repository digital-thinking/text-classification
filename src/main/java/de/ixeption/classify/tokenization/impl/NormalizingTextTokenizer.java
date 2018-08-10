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
import smile.nlp.tokenizer.BreakIteratorTokenizer;
import smile.nlp.tokenizer.Tokenizer;

import java.util.Arrays;


public class NormalizingTextTokenizer implements TextTokenizer {


    private static final Logger log = LoggerFactory.getLogger(AbstractBagOfWordsFeatureExtractor.class);
    private final Tokenizer _tokenizer = new BreakIteratorTokenizer();
    private final StopWords _stopWords = EnglishStopWords.GOOGLE;
    private final Normalizer _normalizer = SimpleNormalizer.getInstance();


    @Override
    public Token[] tokenize(TextFeature textFeature) {
        String normalized = _normalizer != null ? _normalizer.normalize(textFeature.getText()) : textFeature.getText();
        String[] split = _tokenizer.split(normalized.toLowerCase());
        return Arrays.stream(split)
                .filter(StringUtils::isNotEmpty)
                .filter(s -> !_stopWords.contains(s))
                .map(Token::new)
                .toArray(Token[]::new);
    }


}
