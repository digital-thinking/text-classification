package de.ixeption.classify.tokenization.impl;

import de.ixeption.classify.features.TextFeature;
import de.ixeption.classify.features.impl.AbstractBagOfWordsFeatureExtractor;
import de.ixeption.classify.pipeline.TokenizedText;
import de.ixeption.classify.tokenization.TextTokenizer;
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
    private Tokenizer _tokenizer = new BreakIteratorTokenizer();
    private StopWords _stopWords = EnglishStopWords.GOOGLE;
    private Normalizer _normalizer = SimpleNormalizer.getInstance();


    public Tokenizer getTokenizer() {
        return _tokenizer;
    }

    public void setTokenizer(Tokenizer _tokenizer) {
        this._tokenizer = _tokenizer;
    }

    public StopWords getStopWords() {
        return _stopWords;
    }

    public void setStopWords(StopWords _stopWords) {
        this._stopWords = _stopWords;
    }

    public Normalizer getNormalizer() {
        return _normalizer;
    }

    public void setNormalizer(Normalizer _normalizer) {
        this._normalizer = _normalizer;
    }

    @Override
    public synchronized TokenizedText tokenize(TextFeature textFeature) {
        String normalized = _normalizer != null ? _normalizer.normalize(textFeature.getText()) : textFeature.getText();
        String[] split = _tokenizer.split(normalized.toLowerCase());
        String[] tokens = Arrays.stream(split)
                .filter(StringUtils::isNotEmpty)
                .filter(s -> !_stopWords.contains(s))
                .toArray(String[]::new);

        return new TokenizedText(tokens, textFeature.getLanguage());
    }


}
